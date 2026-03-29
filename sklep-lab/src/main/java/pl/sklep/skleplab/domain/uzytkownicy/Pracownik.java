package pl.sklep.skleplab.domain.uzytkownicy;

/**
 * Specjalizacja {@link Uzytkownik}: rola pracownika (UML: Pracownik).
 * Na start dane typu numer pracownika nie są używane w flow, więc jest tylko przechowywane.
 */
public class Pracownik extends Uzytkownik {
	private final String numerPracownika;

	public Pracownik(String email, String hasloHash, String numerPracownika) {
		super(email, hasloHash, Rola.EMPLOYEE);
		this.numerPracownika = numerPracownika;
	}

	public String getNumerPracownika() {
		return numerPracownika;
	}
}
