package pl.sklep.skleplab.application;

import java.util.List;
import java.util.Optional;

import pl.sklep.skleplab.domain.Zamowienie;

public interface ZamowienieRepository {

	/** Kolejny identyfikator zamówienia (w pamięci — później np. sekwencja w bazie). */
	long nastepnyId();

	Zamowienie save(Zamowienie zamowienie);

	List<Zamowienie> findAll();

	Optional<Zamowienie> findById(Long id);
}
