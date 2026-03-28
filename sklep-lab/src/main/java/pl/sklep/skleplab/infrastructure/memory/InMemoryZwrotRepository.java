package pl.sklep.skleplab.infrastructure.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

import pl.sklep.skleplab.application.ZwrotRepository;
import pl.sklep.skleplab.domain.Zwrot;

@Component
public class InMemoryZwrotRepository implements ZwrotRepository {

	private final AtomicLong kolejnyId = new AtomicLong(1);
	private final Map<Long, Zwrot> zwroty = new ConcurrentHashMap<>();

	@Override
	public long nastepnyId() {
		return kolejnyId.getAndIncrement();
	}

	@Override
	public Zwrot save(Zwrot zwrot) {
		zwroty.put(zwrot.getId(), zwrot);
		return zwrot;
	}

	@Override
	public List<Zwrot> findAll() {
		return new ArrayList<>(zwroty.values());
	}

	@Override
	public Optional<Zwrot> findById(Long id) {
		return Optional.ofNullable(zwroty.get(id));
	}
}
