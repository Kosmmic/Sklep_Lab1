package pl.sklep.skleplab.application.port;

import java.util.List;
import java.util.Optional;

import pl.sklep.skleplab.domain.zamowienie.Zamowienie;

public interface SekretarzZamowien {

	long nastepnyIdZamowienia();

	void zakolejkujPoPlatnosci(String uzytkownik, Zamowienie zamowienie);

	List<Zamowienie> pobierzWszystkieZamowienia();

	Optional<Zamowienie> znajdzZamowienie(long id);

	void zapiszZamowienieWBackendzie(Zamowienie zamowienie);
}
