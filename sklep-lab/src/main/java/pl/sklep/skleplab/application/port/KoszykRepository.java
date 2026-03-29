package pl.sklep.skleplab.application.port;

import pl.sklep.skleplab.domain.koszyk.Koszyk;

public interface KoszykRepository {
	Koszyk pobierzDlaUzytkownika(String username);

	void zapisz(String username, Koszyk koszyk);
}
