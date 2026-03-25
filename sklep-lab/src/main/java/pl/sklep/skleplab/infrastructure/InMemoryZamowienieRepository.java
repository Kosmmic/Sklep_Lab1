package pl.sklep.skleplab.infrastructure;

import java.util.ArrayList;
import java.util.Comparator;
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

	private final Map<Long, Zamowienie> zamowienia = new ConcurrentHashMap<>();
	private final AtomicLong nextId = new AtomicLong(1);

	@Override
	public long nastepnyId() {
		return nextId.getAndIncrement();
	}

	@Override
	public Zamowienie save(Zamowienie zamowienie) {
		zamowienia.put(zamowienie.getId(), zamowienie);
		return zamowienie;
	}

	@Override
	public List<Zamowienie> findAll() {
		List<Zamowienie> lista = new ArrayList<>(zamowienia.values());
		lista.sort(Comparator.comparing(Zamowienie::getId));
		return lista;
	}

	@Override
	public Optional<Zamowienie> findById(Long id) {
		return Optional.ofNullable(zamowienia.get(id));
	}
}
