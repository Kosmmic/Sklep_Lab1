package pl.sklep.skleplab.application;

import pl.sklep.skleplab.domain.Koszyk;

public interface Magazyn {

	void zarezerwuj(String uzytkownik, Long towarId, int docelowaIloscWLiniiKoszyka, int stanMagazynowyFizyczny);

	void zwolnijRezerwacjeKoszyka(String uzytkownik, Koszyk koszyk);

	void potwierdzPlatnoscIZrealizujSprzedaz(String uzytkownik, Koszyk koszyk, TowarCatalog towarCatalog);
}
