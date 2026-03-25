package pl.sklep.skleplab.application;

import java.util.List;
import java.util.Optional;

import pl.sklep.skleplab.domain.Towar;

/**
 * Port aplikacyjny: dostęp do katalogu towarów. Później zamienisz implementację na pliki lub JPA.
 */
public interface TowarCatalog {

	List<Towar> findAll();

	Optional<Towar> findById(Long id);
}
