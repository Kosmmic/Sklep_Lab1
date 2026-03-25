package pl.sklep.skleplab.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Koszyk — agregat z pozycjami; na razie jeden wspólny koszyk w aplikacji (demo).
 */
public class Koszyk {

	private final List<PozycjaKoszyka> pozycje = new ArrayList<>();

	public void dodaj(Towar towar, int ilosc) {
		Objects.requireNonNull(towar);
		if (ilosc <= 0) {
			throw new IllegalArgumentException("Ilość musi być dodatnia");
		}
		if (ilosc > towar.getStanMagazynowy()) {
			throw new IllegalArgumentException("Brak wystarczającego stanu magazynowego");
		}
		Optional<PozycjaKoszyka> istniejaca = pozycje.stream()
				.filter(p -> p.getTowarId().equals(towar.getId()))
				.findFirst();
		if (istniejaca.isPresent()) {
			PozycjaKoszyka p = istniejaca.get();
			int nowaIlosc = p.getIlosc() + ilosc;
			if (nowaIlosc > towar.getStanMagazynowy()) {
				throw new IllegalArgumentException("Brak wystarczającego stanu magazynowego");
			}
			p.setIlosc(nowaIlosc);
		}
		else {
			pozycje.add(new PozycjaKoszyka(towar.getId(), ilosc, towar.getCena()));
		}
	}

	public void usun(Long towarId) {
		pozycje.removeIf(p -> p.getTowarId().equals(towarId));
	}

	public BigDecimal obliczSume() {
		return pozycje.stream()
				.map(PozycjaKoszyka::wartoscLiniowa)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public List<PozycjaKoszyka> getPozycje() {
		return Collections.unmodifiableList(pozycje);
	}

	/** Po złożeniu zamówienia koszyk jest czyszczony (scenariusz z koszyka do zamówienia). */
	public void wyczysc() {
		pozycje.clear();
	}

	public boolean jestPusty() {
		return pozycje.isEmpty();
	}
}
