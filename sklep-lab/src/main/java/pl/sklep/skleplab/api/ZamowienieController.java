package pl.sklep.skleplab.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import pl.sklep.skleplab.application.service.ZamowienieService;
import pl.sklep.skleplab.domain.zamowienie.MetodaPlatnosci;
import pl.sklep.skleplab.domain.zamowienie.Zamowienie;

@RestController
@RequestMapping("/api/v1/zamowienia")
public class ZamowienieController {

	private final ZamowienieService zamowienieService;

	public ZamowienieController(ZamowienieService zamowienieService) {
		this.zamowienieService = zamowienieService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ZamowienieOdpowiedz zloz(@RequestBody ZlozZamowienieRequest body) {
		MetodaPlatnosci mp = MetodaPlatnosci.valueOf(body.metodaPlatnosci());
		Zamowienie z = zamowienieService.zlozZamowienie(mp);
		return ZamowienieOdpowiedz.from(z);
	}

	@GetMapping
	public List<ZamowienieOdpowiedz> lista() {
		return zamowienieService.listaZamowien().stream().map(ZamowienieOdpowiedz::from).toList();
	}

	@PostMapping("/{id}/wysylka")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void zatwierdzWysylke(@PathVariable long id) {
		zamowienieService.zatwierdzDoWysylki(id);
	}

	public record ZlozZamowienieRequest(String metodaPlatnosci) {
	}

	public record ZamowienieOdpowiedz(long id, String status, String nrFaktury, String metodaPlatnosci) {

		static ZamowienieOdpowiedz from(Zamowienie z) {
			return new ZamowienieOdpowiedz(
					z.getId(),
					z.getStatusZamowienia().name(),
					z.getSprzedaz().getNrFaktury(),
					z.getMetodaPlatnosci().name());
		}
	}
}
