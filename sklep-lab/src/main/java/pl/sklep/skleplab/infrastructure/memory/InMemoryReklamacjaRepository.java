package pl.sklep.skleplab.infrastructure.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

import pl.sklep.skleplab.application.port.ReklamacjaRepository;
import pl.sklep.skleplab.domain.zgloszenie.Reklamacja;

@Component
public class InMemoryReklamacjaRepository implements ReklamacjaRepository {

	private final AtomicLong kolejnyId = new AtomicLong(1);
	private final Map<Long, Reklamacja> reklamacje = new ConcurrentHashMap<>();

	@Override
	public long nastepnyId() {
		return kolejnyId.getAndIncrement();
	}

	@Override
	public Reklamacja save(Reklamacja reklamacja) {
		reklamacje.put(reklamacja.getId(), reklamacja);
		return reklamacja;
	}

	@Override
	public List<Reklamacja> findAll() {
		return new ArrayList<>(reklamacje.values());
	}

	@Override
	public Optional<Reklamacja> findById(Long id) {
		return Optional.ofNullable(reklamacje.get(id));
	}
}
