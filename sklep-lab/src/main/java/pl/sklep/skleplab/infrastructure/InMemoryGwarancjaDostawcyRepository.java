package pl.sklep.skleplab.infrastructure;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

import pl.sklep.skleplab.application.GwarancjaDostawcyRepository;
import pl.sklep.skleplab.domain.GwarancjaDostawcy;

@Component
public class InMemoryGwarancjaDostawcyRepository implements GwarancjaDostawcyRepository {

	private final Map<Long, GwarancjaDostawcy> gwarancje = new ConcurrentHashMap<>();
	private final AtomicLong nextId = new AtomicLong(1);

	@Override
	public long nastepnyId() {
		return nextId.getAndIncrement();
	}

	@Override
	public GwarancjaDostawcy save(GwarancjaDostawcy gwarancja) {
		gwarancje.put(gwarancja.getId(), gwarancja);
		return gwarancja;
	}

	@Override
	public List<GwarancjaDostawcy> findAll() {
		List<GwarancjaDostawcy> lista = new ArrayList<>(gwarancje.values());
		lista.sort(Comparator.comparing(GwarancjaDostawcy::getId));
		return lista;
	}

	@Override
	public Optional<GwarancjaDostawcy> findById(Long id) {
		return Optional.ofNullable(gwarancje.get(id));
	}
}

