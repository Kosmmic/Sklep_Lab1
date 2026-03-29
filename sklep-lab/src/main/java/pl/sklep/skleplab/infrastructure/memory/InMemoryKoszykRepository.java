package pl.sklep.skleplab.infrastructure.memory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import pl.sklep.skleplab.application.port.KoszykRepository;
import pl.sklep.skleplab.domain.koszyk.Koszyk;

@Component
public class InMemoryKoszykRepository implements KoszykRepository {

	private final Map<String, Koszyk> koszyki = new ConcurrentHashMap<>();

	@Override
	public Koszyk pobierzDlaUzytkownika(String username) {
		return koszyki.computeIfAbsent(username, u -> new Koszyk());
	}

	@Override
	public void zapisz(String username, Koszyk koszyk) {
		koszyki.put(username, koszyk);
	}
}
