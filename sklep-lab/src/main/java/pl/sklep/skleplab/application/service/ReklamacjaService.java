package pl.sklep.skleplab.application.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import pl.sklep.skleplab.application.port.ReklamacjaRepository;
import pl.sklep.skleplab.application.port.ZamowienieRepository;
import pl.sklep.skleplab.domain.zgloszenie.Reklamacja;
import pl.sklep.skleplab.domain.zgloszenie.StatusZgloszeniaProduktowego;

@Service
public class ReklamacjaService {

	private final ZamowienieRepository zamowienieRepository;
	private final ReklamacjaRepository reklamacjaRepository;

	public ReklamacjaService(ZamowienieRepository zamowienieRepository, ReklamacjaRepository reklamacjaRepository) {
		this.zamowienieRepository = zamowienieRepository;
		this.reklamacjaRepository = reklamacjaRepository;
	}

	public Reklamacja zglosReklamacje(Long idZamowienia, List<?> listaPozycji, String powod, String opisUsterki) {
		// listaPozycji nie jest jeszcze używana (brak modelu pozycji w reklamacji w aktualnym kodzie),
		// ale zachowujemy sygnaturę, żeby łatwo to dodać później.
		zamowienieRepository.findById(idZamowienia)
				.orElseThrow(() -> new IllegalArgumentException("Brak zamówienia o id: " + idZamowienia));

		long id = reklamacjaRepository.nastepnyId();
		Reklamacja r = new Reklamacja(id, idZamowienia, Instant.now(),
				StatusZgloszeniaProduktowego.ZGLOSZONE, powod, opisUsterki);
		return reklamacjaRepository.save(r);
	}

	public void weryfikujStan(Long idReklamacji, String opisStanu) {
		Reklamacja r = reklamacjaRepository.findById(idReklamacji)
				.orElseThrow(() -> new IllegalArgumentException("Brak reklamacji o id: " + idReklamacji));
		r.weryfikuj(opisStanu);
		reklamacjaRepository.save(r);
	}

	public void dodajZdjecia(Long idReklamacji, List<String> linki) {
		Reklamacja r = reklamacjaRepository.findById(idReklamacji)
				.orElseThrow(() -> new IllegalArgumentException("Brak reklamacji o id: " + idReklamacji));
		r.dodajZdjeciaWeryfikacyjne(linki);
		reklamacjaRepository.save(r);
	}

	public void akceptuj(Long idReklamacji) {
		Reklamacja r = reklamacjaRepository.findById(idReklamacji)
				.orElseThrow(() -> new IllegalArgumentException("Brak reklamacji o id: " + idReklamacji));
		r.akceptuj();
		reklamacjaRepository.save(r);
	}

	public void odrzuc(Long idReklamacji, String uzasadnienie) {
		Reklamacja r = reklamacjaRepository.findById(idReklamacji)
				.orElseThrow(() -> new IllegalArgumentException("Brak reklamacji o id: " + idReklamacji));
		r.odrzuc(uzasadnienie);
		reklamacjaRepository.save(r);
	}

	public void zlecZwrotDoDostawcy(Long idReklamacji) {
		Reklamacja r = reklamacjaRepository.findById(idReklamacji)
				.orElseThrow(() -> new IllegalArgumentException("Brak reklamacji o id: " + idReklamacji));
		r.zlecZwrotDoDostawcy();
		reklamacjaRepository.save(r);
	}
}
