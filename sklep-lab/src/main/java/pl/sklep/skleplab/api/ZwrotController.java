package pl.sklep.skleplab.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import pl.sklep.skleplab.application.ZwrotService;
import pl.sklep.skleplab.domain.Zwrot;

@RestController
@RequestMapping("/api/v1")
public class ZwrotController {

	private final ZwrotService zwrotService;

	public ZwrotController(ZwrotService zwrotService) {
		this.zwrotService = zwrotService;
	}

	@PostMapping("/zamowienia/{id}/zwrot")
	@ResponseStatus(HttpStatus.CREATED)
	public ZwrotOdpowiedz zglosZwrot(@PathVariable long id, @RequestBody ZglosZwrotRequest request) {
		Zwrot z = zwrotService.zglosZwrot(id, request.pozycje(), request.powod());
		return ZwrotOdpowiedz.from(z);
	}

	@PostMapping("/zwroty/{id}/weryfikacja")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void weryfikuj(@PathVariable long id, @RequestBody WeryfikujZwrotRequest request) {
		zwrotService.weryfikujZwrot(id, request.opisStanu());
	}

	@PostMapping("/zwroty/{id}/odbior")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void potwierdzOdbior(@PathVariable long id) {
		zwrotService.potwierdzOdbiorPaczki(id);
	}

	@PostMapping("/zwroty/{id}/akceptacja")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void akceptuj(@PathVariable long id) {
		zwrotService.akceptujZwrot(id);
	}

	@PostMapping("/zwroty/{id}/odrzucenie")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void odrzuc(@PathVariable long id, @RequestBody OdrzucZwrotRequest request) {
		zwrotService.odrzucZwrot(id, request.uzasadnienie());
	}

	@PostMapping("/zwroty/{id}/zwrot-srodkow")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void zwrocSrodki(@PathVariable long id) {
		zwrotService.zlećZwrotSrodkow(id);
	}

	public record PozycjaZwrotuRequest(Long towarId, int ilosc) {
	}

	public record ZglosZwrotRequest(String powod, List<PozycjaZwrotuRequest> pozycje) {
	}

	public record WeryfikujZwrotRequest(String opisStanu) {
	}

	public record OdrzucZwrotRequest(String uzasadnienie) {
	}

	public record ZwrotOdpowiedz(long id, long idZamowienia, String statusDecyzji) {
		static ZwrotOdpowiedz from(Zwrot z) {
			return new ZwrotOdpowiedz(
					z.getId(),
					z.getIdZamowienia(),
					z.getStatusDecyzji().name());
		}
	}
}

