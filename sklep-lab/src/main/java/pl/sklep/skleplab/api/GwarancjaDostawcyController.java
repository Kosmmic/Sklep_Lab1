package pl.sklep.skleplab.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import pl.sklep.skleplab.application.service.GwarancjaDostawcyService;
import pl.sklep.skleplab.domain.zgloszenie.GwarancjaDostawcy;
import pl.sklep.skleplab.domain.zgloszenie.StatusZgloszeniaProduktowego;

@RestController
@RequestMapping("/api/v1")
public class GwarancjaDostawcyController {

	private final GwarancjaDostawcyService service;

	public GwarancjaDostawcyController(GwarancjaDostawcyService service) {
		this.service = service;
	}

	@PostMapping("/zamowienia/{id}/gwarancja")
	@ResponseStatus(HttpStatus.CREATED)
	public GwarancjaOdpowiedz zglos(@PathVariable long id, @RequestBody ZglosGwarancjeRequest request) {
		GwarancjaDostawcy g = service.zglosGwarancje(id, request.pozycje(), request.powod(), request.wymaganyTryb());
		return GwarancjaOdpowiedz.from(g);
	}

	@PostMapping("/gwarancje/{id}/weryfikacja")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void weryfikuj(@PathVariable long id, @RequestBody WeryfikujGwarancjeRequest request) {
		service.weryfikujStan(id, request.opisStanu());
	}

	@PostMapping("/gwarancje/{id}/zdjecia")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void dodajZdjecia(@PathVariable long id, @RequestBody DodajZdjeciaRequest request) {
		service.dodajZdjecia(id, request.linki());
	}

	@PostMapping("/gwarancje/{id}/akceptacja")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void akceptuj(@PathVariable long id) {
		service.akceptuj(id);
	}

	@PostMapping("/gwarancje/{id}/odrzucenie")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void odrzuc(@PathVariable long id, @RequestBody OdrzucGwarancjeRequest request) {
		service.odrzuc(id, request.uzasadnienie());
	}

	@PostMapping("/gwarancje/{id}/zwrot-do-dostawcy")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void zwrocDoDostawcy(@PathVariable long id) {
		service.zlecZwrotDoDostawcy(id);
	}

	public record PozycjaGwarancjiRequest(Long towarId, int ilosc) {
	}

	public record ZglosGwarancjeRequest(String powod, String wymaganyTryb, List<PozycjaGwarancjiRequest> pozycje) {
	}

	public record WeryfikujGwarancjeRequest(String opisStanu) {
	}

	public record DodajZdjeciaRequest(List<String> linki) {
	}

	public record OdrzucGwarancjeRequest(String uzasadnienie) {
	}

	public record GwarancjaOdpowiedz(long id, long idZamowienia, StatusZgloszeniaProduktowego status) {
		static GwarancjaOdpowiedz from(GwarancjaDostawcy g) {
			return new GwarancjaOdpowiedz(g.getId(), g.getIdZamowienia(), g.getStatus());
		}
	}
}

