package pl.sklep.skleplab.application;

import pl.sklep.skleplab.domain.Koszyk;

public interface KoszykRepository {
	Koszyk pobierzDlaUzytkownika(String username);

	void zapisz(String username, Koszyk koszyk);
}

