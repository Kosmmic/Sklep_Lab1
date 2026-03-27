package pl.sklep.skleplab.domain;

/**
 * Specjalizacja {@link Uzytkownik}: rola klienta (UML: Klient).
 */
public class Klient extends Uzytkownik {

	public Klient(String email, String hasloHash) {
		super(email, hasloHash, Rola.CLIENT);
	}
}

