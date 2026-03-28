package pl.sklep.skleplab.infrastructure.memory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import pl.sklep.skleplab.application.TowarCatalog;
import pl.sklep.skleplab.domain.Towar;

@Component
public class InMemoryTowarCatalog implements TowarCatalog {

	private final Map<Long, Towar> towary = new ConcurrentHashMap<>();

	public InMemoryTowarCatalog() {
		zapisz(new Towar(1L, "Laptop", new BigDecimal("3500.00"), 50, "Elektronika"));
		zapisz(new Towar(2L, "Mysz", new BigDecimal("89.99"), 200, "Akcesoria"));
		zapisz(new Towar(3L, "Klawiatura", new BigDecimal("249.00"), 30, "Akcesoria"));
	}

	private void zapisz(Towar towar) {
		towary.put(towar.getId(), towar);
	}

	@Override
	public List<Towar> findAll() {
		return new ArrayList<>(towary.values());
	}

	@Override
	public Optional<Towar> findById(Long id) {
		return Optional.ofNullable(towary.get(id));
	}

	@Override
	public void zmniejszStanMagazynowy(Long towarId, int ilosc) {
		Towar t = towary.get(towarId);
		if (t == null) {
			throw new IllegalArgumentException("Nieznany towar: " + towarId);
		}
		int nowyStan = t.getStanMagazynowy() - ilosc;
		if (nowyStan < 0) {
			throw new IllegalArgumentException("Stan magazynowy nie może być ujemny");
		}
		towary.put(towarId,
				new Towar(t.getId(), t.getNazwa(), t.getCena(), nowyStan, t.getKategoria()));
	}
}
