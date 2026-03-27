package pl.sklep.skleplab.application;

import java.util.List;
import java.util.Optional;

import pl.sklep.skleplab.domain.GwarancjaDostawcy;

public interface GwarancjaDostawcyRepository {

	long nastepnyId();

	GwarancjaDostawcy save(GwarancjaDostawcy gwarancja);

	List<GwarancjaDostawcy> findAll();

	Optional<GwarancjaDostawcy> findById(Long id);
}

