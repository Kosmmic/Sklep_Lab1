package pl.sklep.skleplab.infrastructure;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

import pl.sklep.skleplab.application.ReklamacjaRepository;
import pl.sklep.skleplab.domain.Reklamacja;

@Component
public class InMemoryReklamacjaRepository implements ReklamacjaRepository {

	private final Map<Long, Reklamacja> reklamacje = new ConcurrentHashMap<>();
	private final AtomicLong nextId = new AtomicLong(1);

	@Override
	public long nastepnyId() {
		return nextId.getAndIncrement();
	}

	@Override
	public Reklamacja save(Reklamacja reklamacja) {
		reklamacje.put(reklamacja.getId(), reklamacja);
		return reklamacja;
	}

	@Override
	public List<Reklamacja> findAll() {
		List<Reklamacja> lista = new ArrayList<>(reklamacje.values());
		lista.sort(Comparator.comparing(Reklamacja::getId));
		return lista;
	}

	@Override
	public Optional<Reklamacja> findById(Long id) {
		return Optional.ofNullable(reklamacje.get(id));
	}
}

