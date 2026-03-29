package pl.sklep.skleplab.application.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import pl.sklep.skleplab.application.port.GwarancjaDostawcyRepository;
import pl.sklep.skleplab.application.port.ZamowienieRepository;
import pl.sklep.skleplab.domain.zgloszenie.GwarancjaDostawcy;
import pl.sklep.skleplab.domain.zgloszenie.StatusZgloszeniaProduktowego;

@Service
public class GwarancjaDostawcyService {

	private final ZamowienieRepository zamowienieRepository;
	private final GwarancjaDostawcyRepository gwarancjaRepository;

	public GwarancjaDostawcyService(ZamowienieRepository zamowienieRepository, GwarancjaDostawcyRepository gwarancjaRepository) {
		this.zamowienieRepository = zamowienieRepository;
		this.gwarancjaRepository = gwarancjaRepository;
	}

	public GwarancjaDostawcy zglosGwarancje(Long idZamowienia, List<?> listaPozycji, String powod, String wymaganyTryb) {
		zamowienieRepository.findById(idZamowienia)
				.orElseThrow(() -> new IllegalArgumentException("Brak zamówienia o id: " + idZamowienia));

		long id = gwarancjaRepository.nastepnyId();
		GwarancjaDostawcy g = new GwarancjaDostawcy(id, idZamowienia, Instant.now(),
				StatusZgloszeniaProduktowego.ZGLOSZONE, powod, wymaganyTryb);
		return gwarancjaRepository.save(g);
	}

	public void weryfikujStan(Long idGwarancji, String opisStanu) {
		GwarancjaDostawcy g = gwarancjaRepository.findById(idGwarancji)
				.orElseThrow(() -> new IllegalArgumentException("Brak gwarancji o id: " + idGwarancji));
		g.weryfikuj(opisStanu);
		gwarancjaRepository.save(g);
	}

	public void dodajZdjecia(Long idGwarancji, List<String> linki) {
		GwarancjaDostawcy g = gwarancjaRepository.findById(idGwarancji)
				.orElseThrow(() -> new IllegalArgumentException("Brak gwarancji o id: " + idGwarancji));
		g.dodajZdjeciaWeryfikacyjne(linki);
		gwarancjaRepository.save(g);
	}

	public void akceptuj(Long idGwarancji) {
		GwarancjaDostawcy g = gwarancjaRepository.findById(idGwarancji)
				.orElseThrow(() -> new IllegalArgumentException("Brak gwarancji o id: " + idGwarancji));
		g.akceptuj();
		gwarancjaRepository.save(g);
	}

	public void odrzuc(Long idGwarancji, String uzasadnienie) {
		GwarancjaDostawcy g = gwarancjaRepository.findById(idGwarancji)
				.orElseThrow(() -> new IllegalArgumentException("Brak gwarancji o id: " + idGwarancji));
		g.odrzuc(uzasadnienie);
		gwarancjaRepository.save(g);
	}

	public void zlecZwrotDoDostawcy(Long idGwarancji) {
		GwarancjaDostawcy g = gwarancjaRepository.findById(idGwarancji)
				.orElseThrow(() -> new IllegalArgumentException("Brak gwarancji o id: " + idGwarancji));
		g.zlecZwrotDoDostawcy();
		gwarancjaRepository.save(g);
	}
}
