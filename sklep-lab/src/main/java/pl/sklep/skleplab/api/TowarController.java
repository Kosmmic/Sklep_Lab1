package pl.sklep.skleplab.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.sklep.skleplab.application.port.TowarCatalog;
import pl.sklep.skleplab.domain.katalog.Towar;

@RestController
@RequestMapping("/api/v1")
public class TowarController {

	private final TowarCatalog towarCatalog;

	public TowarController(TowarCatalog towarCatalog) {
		this.towarCatalog = towarCatalog;
	}

	@GetMapping("/towary")
	public List<Towar> listaTowarow() {
		return towarCatalog.findAll();
	}
}
