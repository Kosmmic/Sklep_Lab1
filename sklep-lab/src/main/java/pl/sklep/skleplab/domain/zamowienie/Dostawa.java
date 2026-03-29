package pl.sklep.skleplab.domain.zamowienie;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Przesyłka 1:1 z zamówieniem. Nazwy pól zgodne z intencją UML (poprawka literówek {@code data dataWystawienia} → jedna data).
 */
public class Dostawa {

	private final String numerPrzesylki;
	private String statusPrzesylki;
	private final String dostawca;
	private final LocalDate dataNadania;
	private LocalDate dataDostarczenia;
	private final LocalDate przewidywanaData;

	public Dostawa(
			String numerPrzesylki,
			String statusPrzesylki,
			String dostawca,
			LocalDate dataNadania,
			LocalDate dataDostarczenia,
			LocalDate przewidywanaData) {
		this.numerPrzesylki = numerPrzesylki;
		this.statusPrzesylki = Objects.requireNonNull(statusPrzesylki);
		this.dostawca = Objects.requireNonNull(dostawca);
		this.dataNadania = Objects.requireNonNull(dataNadania);
		this.dataDostarczenia = dataDostarczenia;
		this.przewidywanaData = Objects.requireNonNull(przewidywanaData);
	}

	public String getNumerPrzesylki() {
		return numerPrzesylki;
	}

	public String getStatusPrzesylki() {
		return statusPrzesylki;
	}

	public void setStatusPrzesylki(String statusPrzesylki) {
		this.statusPrzesylki = Objects.requireNonNull(statusPrzesylki);
	}

	public String getDostawca() {
		return dostawca;
	}

	public LocalDate getDataNadania() {
		return dataNadania;
	}

	public LocalDate getDataDostarczenia() {
		return dataDostarczenia;
	}

	public void setDataDostarczenia(LocalDate dataDostarczenia) {
		this.dataDostarczenia = dataDostarczenia;
	}

	public LocalDate getPrzewidywanaData() {
		return przewidywanaData;
	}
}
