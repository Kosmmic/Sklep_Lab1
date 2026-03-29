package pl.sklep.skleplab.domain.zamowienie;

import java.math.BigDecimal;
import java.util.Objects;

import pl.sklep.skleplab.domain.koszyk.PozycjaKoszyka;

/**
 * Pozycja zamówienia — snapshot ceny i podatku (jak w UML).
 */
public class PozycjaZamowienia {

	private final Long towarId;
	private final int ilosc;
	private final BigDecimal cenaSnapshot;
	private final BigDecimal podatek;

	public PozycjaZamowienia(Long towarId, int ilosc, BigDecimal cenaSnapshot, BigDecimal podatek) {
		this.towarId = Objects.requireNonNull(towarId);
		this.ilosc = ilosc;
		this.cenaSnapshot = Objects.requireNonNull(cenaSnapshot);
		this.podatek = podatek != null ? podatek : BigDecimal.ZERO;
	}

	public static PozycjaZamowienia zKoszyka(PozycjaKoszyka linia) {
		// Na start: podatek 0 — później możesz dodać stawkę VAT per kategoria towaru.
		return new PozycjaZamowienia(
				linia.getTowarId(),
				linia.getIlosc(),
				linia.getCenaWChwiliDodania(),
				BigDecimal.ZERO);
	}

	public Long getTowarId() {
		return towarId;
	}

	public int getIlosc() {
		return ilosc;
	}

	public BigDecimal getCenaSnapshot() {
		return cenaSnapshot;
	}

	public BigDecimal getPodatek() {
		return podatek;
	}

	/** Wartość linii: cena jednostkowa × ilość + podatek (traktowany jako kwota dla całej linii). */
	public BigDecimal wartoscBrutto() {
		return cenaSnapshot.multiply(BigDecimal.valueOf(ilosc)).add(podatek);
	}
}
