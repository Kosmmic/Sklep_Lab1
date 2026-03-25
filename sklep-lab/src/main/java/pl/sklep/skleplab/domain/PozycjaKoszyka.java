package pl.sklep.skleplab.domain;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Pozycja w koszyku — cena zapisana w momencie dodania (snapshot), jak w UML.
 */
public class PozycjaKoszyka {

	private final Long towarId;
	private int ilosc;
	private final BigDecimal cenaWChwiliDodania;

	public PozycjaKoszyka(Long towarId, int ilosc, BigDecimal cenaWChwiliDodania) {
		this.towarId = Objects.requireNonNull(towarId);
		this.ilosc = ilosc;
		this.cenaWChwiliDodania = Objects.requireNonNull(cenaWChwiliDodania);
	}

	public Long getTowarId() {
		return towarId;
	}

	public int getIlosc() {
		return ilosc;
	}

	public void setIlosc(int ilosc) {
		this.ilosc = ilosc;
	}

	public BigDecimal getCenaWChwiliDodania() {
		return cenaWChwiliDodania;
	}

	public BigDecimal wartoscLiniowa() {
		return cenaWChwiliDodania.multiply(BigDecimal.valueOf(ilosc));
	}
}
