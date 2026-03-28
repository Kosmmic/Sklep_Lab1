package pl.sklep.skleplab.infrastructure.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

import pl.sklep.skleplab.application.ZamowienieRepository;
import pl.sklep.skleplab.domain.Zamowienie;

@Component
public class InMemoryZamowienieRepository implements ZamowienieRepository {

	private final AtomicLong kolejnyId = new AtomicLong(1);
	private final Map<Long, Zamowienie> zamowienia = new ConcurrentHashMap<>();

	@Override
	public long nastepnyId() {
		return kolejnyId.getAndIncrement();
	}

	@Override
	public Zamowienie save(Zamowienie zamowienie) {
		zamowienia.put(zamowienie.getId(), zamowienie);
		return zamowienie;
	}

	@Override
	public List<Zamowienie> findAll() {
		return new ArrayList<>(zamowienia.values());
	}

	@Override
	public Optional<Zamowienie> findById(Long id) {
		return Optional.ofNullable(zamowienia.get(id));
	}
}
