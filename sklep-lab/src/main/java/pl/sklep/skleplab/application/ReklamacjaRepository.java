package pl.sklep.skleplab.application;

import java.util.List;
import java.util.Optional;

import pl.sklep.skleplab.domain.Reklamacja;

public interface ReklamacjaRepository {

	long nastepnyId();

	Reklamacja save(Reklamacja reklamacja);

	List<Reklamacja> findAll();

	Optional<Reklamacja> findById(Long id);
}

