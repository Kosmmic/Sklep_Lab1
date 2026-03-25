package pl.sklep.skleplab.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Dokument sprzedaży powiązany 1:1 z zamówieniem (jak w UML: {@code Zamowienie} — {@code Sprzedaz}).
 */
public class Sprzedaz {

	private final String nrFaktury;
	private final BigDecimal kwotaBrutto;
	private final BigDecimal kwotaNetto;
	private final LocalDate dataWystawienia;

	public Sprzedaz(String nrFaktury, BigDecimal kwotaBrutto, BigDecimal kwotaNetto, LocalDate dataWystawienia) {
		this.nrFaktury = Objects.requireNonNull(nrFaktury);
		this.kwotaBrutto = Objects.requireNonNull(kwotaBrutto);
		this.kwotaNetto = Objects.requireNonNull(kwotaNetto);
		this.dataWystawienia = Objects.requireNonNull(dataWystawienia);
	}

	public String getNrFaktury() {
		return nrFaktury;
	}

	public BigDecimal getKwotaBrutto() {
		return kwotaBrutto;
	}

	public BigDecimal getKwotaNetto() {
		return kwotaNetto;
	}

	public LocalDate getDataWystawienia() {
		return dataWystawienia;
	}
}
