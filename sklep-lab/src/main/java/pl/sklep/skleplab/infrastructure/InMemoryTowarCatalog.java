package pl.sklep.skleplab.infrastructure;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import pl.sklep.skleplab.application.TowarCatalog;
import pl.sklep.skleplab.domain.Towar;

/**
 * {@code @Component} — klasa „bean”: Spring tworzy jedną instancję i wstrzykuje tam, gdzie potrzeba.
 * Dane trzymamy w pamięci (mapa); po restarcie aplikacji znikają — na start nauki to OK.
 */
@Component
public class InMemoryTowarCatalog implements TowarCatalog {

	private final Map<Long, Towar> towary = new ConcurrentHashMap<>();

	public InMemoryTowarCatalog() {
		zasilPrzykladowymiDanymi();
	}

	private void zasilPrzykladowymiDanymi() {
		towary.put(1L, new Towar(1L, "Laptop", new BigDecimal("3499.00"), 5, "Elektronika"));
		towary.put(2L, new Towar(2L, "Mysz", new BigDecimal("89.99"), 50, "Akcesoria"));
		towary.put(3L, new Towar(3L, "Klawiatura", new BigDecimal("249.00"), 20, "Akcesoria"));
	}

	@Override
	public List<Towar> findAll() {
		return new ArrayList<>(towary.values());
	}

	@Override
	public Optional<Towar> findById(Long id) {
		return Optional.ofNullable(towary.get(id));
	}
}
