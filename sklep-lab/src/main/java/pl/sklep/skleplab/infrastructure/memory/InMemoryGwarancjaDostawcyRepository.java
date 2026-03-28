package pl.sklep.skleplab.infrastructure.memory;

import java.util.ArrayList;
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

	private final AtomicLong kolejnyId = new AtomicLong(1);
	private final Map<Long, GwarancjaDostawcy> gwarancje = new ConcurrentHashMap<>();

	@Override
	public long nastepnyId() {
		return kolejnyId.getAndIncrement();
	}

	@Override
	public GwarancjaDostawcy save(GwarancjaDostawcy gwarancja) {
		gwarancje.put(gwarancja.getId(), gwarancja);
		return gwarancja;
	}

	@Override
	public List<GwarancjaDostawcy> findAll() {
		return new ArrayList<>(gwarancje.values());
	}

	@Override
	public Optional<GwarancjaDostawcy> findById(Long id) {
		return Optional.ofNullable(gwarancje.get(id));
	}
}
