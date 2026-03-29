package pl.sklep.skleplab.application.port;

import java.util.List;
import java.util.Optional;

import pl.sklep.skleplab.domain.katalog.Towar;

public interface TowarCatalog {

	List<Towar> findAll();

	Optional<Towar> findById(Long id);

	void zmniejszStanMagazynowy(Long towarId, int ilosc);
}
