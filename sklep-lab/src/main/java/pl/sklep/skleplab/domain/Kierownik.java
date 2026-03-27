package pl.sklep.skleplab.domain;

/**
 * Specjalizacja {@link Uzytkownik}: rola kierownika (UML: Kierownik).
 */
public class Kierownik extends Uzytkownik {
	private final String dzial;

	public Kierownik(String email, String hasloHash, String dzial) {
		super(email, hasloHash, Rola.MANAGER);
		this.dzial = dzial;
	}

	public String getDzial() {
		return dzial;
	}
}

