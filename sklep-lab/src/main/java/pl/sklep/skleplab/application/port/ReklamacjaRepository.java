package pl.sklep.skleplab.application.port;

import java.util.List;
import java.util.Optional;

import pl.sklep.skleplab.domain.zgloszenie.Reklamacja;

public interface ReklamacjaRepository {

	long nastepnyId();

	Reklamacja save(Reklamacja reklamacja);

	List<Reklamacja> findAll();

	Optional<Reklamacja> findById(Long id);
}
