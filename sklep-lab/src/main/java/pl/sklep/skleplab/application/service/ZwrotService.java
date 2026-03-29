package pl.sklep.skleplab.application.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import pl.sklep.skleplab.application.port.ZamowienieRepository;
import pl.sklep.skleplab.application.port.ZwrotRepository;
import pl.sklep.skleplab.domain.zwrot.Zwrot;

/**
 * Use case: ścieżka zwrotu zgodna z diagramem (uprawnienia ogarniamy w SecurityConfig).
 * Na razie persistence jest in-memory; potem podmienimy repozytorium na pliki .txt.
 */
@Service
public class ZwrotService {

	private final ZamowienieRepository zamowienieRepository;
	private final ZwrotRepository zwrotRepository;

	public ZwrotService(ZamowienieRepository zamowienieRepository, ZwrotRepository zwrotRepository) {
		this.zamowienieRepository = zamowienieRepository;
		this.zwrotRepository = zwrotRepository;
	}

	public Zwrot zglosZwrot(Long idZamowienia, List<?> listaPozycji, String powod) {
		// Uproszczenie: lista pozycji nie jest jeszcze modelowana w domenie zwrotu.
		// Zachowujemy parametr, żeby łatwo dodać walidację/rozbicie na pozycje później.
		zamowienieRepository.findById(idZamowienia)
				.orElseThrow(() -> new IllegalArgumentException("Brak zamówienia o id: " + idZamowienia));

		long id = zwrotRepository.nastepnyId();
		Zwrot z = new Zwrot(id, idZamowienia, powod, Instant.now());
		return zwrotRepository.save(z);
	}

	public void weryfikujZwrot(Long idZwrotu, String opisStanu) {
		Zwrot z = zwrotRepository.findById(idZwrotu)
				.orElseThrow(() -> new IllegalArgumentException("Brak zwrotu o id: " + idZwrotu));
		z.weryfikujStanTowaru(opisStanu);
		zwrotRepository.save(z);
	}

	public void potwierdzOdbiorPaczki(Long idZwrotu) {
		Zwrot z = zwrotRepository.findById(idZwrotu)
				.orElseThrow(() -> new IllegalArgumentException("Brak zwrotu o id: " + idZwrotu));
		z.potwierdzOdbiorPaczki();
		zwrotRepository.save(z);
	}

	public void akceptujZwrot(Long idZwrotu) {
		Zwrot z = zwrotRepository.findById(idZwrotu)
				.orElseThrow(() -> new IllegalArgumentException("Brak zwrotu o id: " + idZwrotu));
		z.akceptujZwrot();
		zwrotRepository.save(z);
	}

	public void odrzucZwrot(Long idZwrotu, String uzasadnienie) {
		Zwrot z = zwrotRepository.findById(idZwrotu)
				.orElseThrow(() -> new IllegalArgumentException("Brak zwrotu o id: " + idZwrotu));
		z.odrzucZwrot(uzasadnienie);
		zwrotRepository.save(z);
	}

	public void zlećZwrotSrodkow(Long idZwrotu) {
		Zwrot z = zwrotRepository.findById(idZwrotu)
				.orElseThrow(() -> new IllegalArgumentException("Brak zwrotu o id: " + idZwrotu));
		z.zlećZwrotSrodkow();
		zwrotRepository.save(z);
	}
}
