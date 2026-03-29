package pl.sklep.skleplab.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import pl.sklep.skleplab.application.service.ReklamacjaService;
import pl.sklep.skleplab.domain.zgloszenie.Reklamacja;
import pl.sklep.skleplab.domain.zgloszenie.StatusZgloszeniaProduktowego;

@RestController
@RequestMapping("/api/v1")
public class ReklamacjaController {

	private final ReklamacjaService reklamacjaService;

	public ReklamacjaController(ReklamacjaService reklamacjaService) {
		this.reklamacjaService = reklamacjaService;
	}

	@PostMapping("/zamowienia/{id}/reklamacja")
	@ResponseStatus(HttpStatus.CREATED)
	public ReklamacjaOdpowiedz zglos(@PathVariable long id, @RequestBody ZglosReklamacjeRequest request) {
		Reklamacja r = reklamacjaService.zglosReklamacje(id, request.pozycje(), request.powod(), request.opisUsterki());
		return ReklamacjaOdpowiedz.from(r);
	}

	@PostMapping("/reklamacje/{id}/weryfikacja")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void weryfikuj(@PathVariable long id, @RequestBody WeryfikujReklamacjeRequest request) {
		reklamacjaService.weryfikujStan(id, request.opisStanu());
	}

	@PostMapping("/reklamacje/{id}/zdjecia")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void dodajZdjecia(@PathVariable long id, @RequestBody DodajZdjeciaRequest request) {
		reklamacjaService.dodajZdjecia(id, request.linki());
	}

	@PostMapping("/reklamacje/{id}/akceptacja")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void akceptuj(@PathVariable long id) {
		reklamacjaService.akceptuj(id);
	}

	@PostMapping("/reklamacje/{id}/odrzucenie")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void odrzuc(@PathVariable long id, @RequestBody OdrzucReklamacjeRequest request) {
		reklamacjaService.odrzuc(id, request.uzasadnienie());
	}

	@PostMapping("/reklamacje/{id}/zwrot-do-dostawcy")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void zwrocDoDostawcy(@PathVariable long id) {
		reklamacjaService.zlecZwrotDoDostawcy(id);
	}

	public record PozycjaReklamacjiRequest(Long towarId, int ilosc) {
	}

	public record ZglosReklamacjeRequest(String powod, String opisUsterki, List<PozycjaReklamacjiRequest> pozycje) {
	}

	public record WeryfikujReklamacjeRequest(String opisStanu) {
	}

	public record DodajZdjeciaRequest(List<String> linki) {
	}

	public record OdrzucReklamacjeRequest(String uzasadnienie) {
	}

	public record ReklamacjaOdpowiedz(long id, long idZamowienia, StatusZgloszeniaProduktowego status) {
		static ReklamacjaOdpowiedz from(Reklamacja r) {
			return new ReklamacjaOdpowiedz(
					r.getId(),
					r.getIdZamowienia(),
					r.getStatus());
		}
	}
}

