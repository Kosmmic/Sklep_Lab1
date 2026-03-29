package pl.sklep.skleplab.domain.uzytkownicy;

/**
 * Specjalizacja {@link Uzytkownik}: rola administratora (UML: Administrator).
 */
public class Administrator extends Uzytkownik {

	public Administrator(String email, String hasloHash) {
		super(email, hasloHash, Rola.ADMIN);
	}
}
