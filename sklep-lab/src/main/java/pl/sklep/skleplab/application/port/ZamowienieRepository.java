package pl.sklep.skleplab.application.port;

import java.util.List;
import java.util.Optional;

import pl.sklep.skleplab.domain.zamowienie.Zamowienie;

public interface ZamowienieRepository {

	long nastepnyId();

	Zamowienie save(Zamowienie zamowienie);

	List<Zamowienie> findAll();

	Optional<Zamowienie> findById(Long id);
}
