package pl.sklep.skleplab.application;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import pl.sklep.skleplab.domain.MetodaPlatnosci;
import pl.sklep.skleplab.domain.StatusZamowienia;
import pl.sklep.skleplab.domain.Zamowienie;

/**
 * Fragment kontraktu jak {@code ProcesZamowieniaKlient}: koszyk → zamówienie.
 * Płatność na razie „w pakiecie” ze złożeniem (demo); później rozdzielisz {@code potwierdzPlatnosc}.
 */
@Service
public class ZamowienieService {

	private final KoszykService koszykService;
	private final ZamowienieRepository zamowienieRepository;

	public ZamowienieService(KoszykService koszykService, ZamowienieRepository zamowienieRepository) {
		this.koszykService = koszykService;
		this.zamowienieRepository = zamowienieRepository;
	}

	public Zamowienie zlozZamowienie(MetodaPlatnosci metodaPlatnosci) {
		var koszyk = koszykService.pobierzKoszyk();
		long id = zamowienieRepository.nastepnyId();
		Zamowienie z = Zamowienie.utworzZKoszyka(id, koszyk, metodaPlatnosci, LocalDate.now());
		z.setStatusZamowienia(StatusZamowienia.OPLACONE);
		zamowienieRepository.save(z);
		koszykService.wyczyscKoszyk();
		return z;
	}

	public List<Zamowienie> listaZamowien() {
		return zamowienieRepository.findAll();
	}

	/**
	 * Odpowiednik {@code ProcesowanieZamowien.zatwierdzDoWysylki} — perspektywa pracownika (demo).
	 */
	public void zatwierdzDoWysylki(long zamowienieId) {
		Zamowienie z = zamowienieRepository.findById(zamowienieId)
				.orElseThrow(() -> new IllegalArgumentException("Brak zamówienia o id: " + zamowienieId));
		if (z.getStatusZamowienia() != StatusZamowienia.OPLACONE) {
			throw new IllegalArgumentException(
					"Wysyłka możliwa tylko dla zamówień opłaconych; aktualny status: " + z.getStatusZamowienia());
		}
		z.setStatusZamowienia(StatusZamowienia.WYSLANE);
		z.getDostawa().setStatusPrzesylki("WYSŁANO");
		zamowienieRepository.save(z);
	}
}
