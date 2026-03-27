package pl.sklep.skleplab.infrastructure;

import java.util.ArrayList;
import java.util.Comparator;
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

	private final Map<Long, Zwrot> zwroty = new ConcurrentHashMap<>();
	private final AtomicLong nextId = new AtomicLong(1);

	@Override
	public long nastepnyId() {
		return nextId.getAndIncrement();
	}

	@Override
	public Zwrot save(Zwrot zwrot) {
		zwroty.put(zwrot.getId(), zwrot);
		return zwrot;
	}

	@Override
	public List<Zwrot> findAll() {
		List<Zwrot> lista = new ArrayList<>(zwroty.values());
		lista.sort(Comparator.comparing(Zwrot::getId));
		return lista;
	}

	@Override
	public Optional<Zwrot> findById(Long id) {
		return Optional.ofNullable(zwroty.get(id));
	}
}

