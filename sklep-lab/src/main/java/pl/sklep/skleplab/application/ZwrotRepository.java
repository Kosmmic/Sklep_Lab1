package pl.sklep.skleplab.application;

import java.util.List;
import java.util.Optional;

import pl.sklep.skleplab.domain.Zwrot;

public interface ZwrotRepository {

	/**
	 * Kolejny identyfikator zwrotu (na razie w pamięci — później pliki).
	 */
	long nastepnyId();

	Zwrot save(Zwrot zwrot);

	List<Zwrot> findAll();

	Optional<Zwrot> findById(Long id);
}

