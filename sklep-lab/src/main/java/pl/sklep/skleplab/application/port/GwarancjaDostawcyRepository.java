package pl.sklep.skleplab.application.port;

import java.util.List;
import java.util.Optional;

import pl.sklep.skleplab.domain.zgloszenie.GwarancjaDostawcy;

public interface GwarancjaDostawcyRepository {

	long nastepnyId();

	GwarancjaDostawcy save(GwarancjaDostawcy gwarancja);

	List<GwarancjaDostawcy> findAll();

	Optional<GwarancjaDostawcy> findById(Long id);
}
