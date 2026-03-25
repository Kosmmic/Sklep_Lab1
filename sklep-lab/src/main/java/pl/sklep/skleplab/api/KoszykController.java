package pl.sklep.skleplab.api;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import pl.sklep.skleplab.application.KoszykService;
import pl.sklep.skleplab.domain.Koszyk;
import pl.sklep.skleplab.domain.PozycjaKoszyka;

@RestController
@RequestMapping("/api/v1/koszyk")
public class KoszykController {

	private final KoszykService koszykService;

	public KoszykController(KoszykService koszykService) {
		this.koszykService = koszykService;
	}

	@GetMapping
	public KoszykOdpowiedz pobierz() {
		Koszyk k = koszykService.pobierzKoszyk();
		return new KoszykOdpowiedz(k.getPozycje(), k.obliczSume());
	}

	@PostMapping("/pozycje")
	@ResponseStatus(HttpStatus.CREATED)
	public void dodajPozycje(@RequestBody DodajPozycjeRequest body) {
		koszykService.dodajDoKoszyka(body.towarId(), body.ilosc());
	}

	/**
	 * Rekord = niemutowalny „worek danych” z JSON; Spring sam mapuje pola z żądania.
	 */
	public record DodajPozycjeRequest(Long towarId, int ilosc) {
	}

	/**
	 * Odpowiedź API: lista pozycji + suma (żeby front nie musiał liczyć).
	 */
	public record KoszykOdpowiedz(List<PozycjaKoszyka> pozycje, BigDecimal suma) {
	}
}
