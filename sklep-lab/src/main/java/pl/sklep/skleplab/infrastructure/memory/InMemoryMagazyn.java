package pl.sklep.skleplab.infrastructure.memory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import pl.sklep.skleplab.application.port.Magazyn;
import pl.sklep.skleplab.application.port.TowarCatalog;
import pl.sklep.skleplab.domain.koszyk.Koszyk;
import pl.sklep.skleplab.domain.koszyk.PozycjaKoszyka;

@Component
public class InMemoryMagazyn implements Magazyn {

	private final Map<String, Map<Long, Integer>> rezerwacje = new ConcurrentHashMap<>();

	@Override
	public void zarezerwuj(String uzytkownik, Long towarId, int docelowaIloscWLiniiKoszyka, int stanMagazynowyFizyczny) {
		if (docelowaIloscWLiniiKoszyka < 0) {
			throw new IllegalArgumentException("Ilość nie może być ujemna");
		}
		synchronized (rezerwacje) {
			int stareWDlaUzytkownika = rezerwacje.getOrDefault(uzytkownik, Map.of()).getOrDefault(towarId, 0);
			int sumaDlaTowaru = sumaRezerwacjiDlaTowaru(towarId);
			int nowaSuma = sumaDlaTowaru - stareWDlaUzytkownika + docelowaIloscWLiniiKoszyka;
			if (nowaSuma > stanMagazynowyFizyczny) {
				throw new IllegalArgumentException("Brak wystarczającego stanu magazynowego (uwzględniając rezerwacje)");
			}
			Map<Long, Integer> linie = new HashMap<>(rezerwacje.getOrDefault(uzytkownik, Map.of()));
			if (docelowaIloscWLiniiKoszyka == 0) {
				linie.remove(towarId);
			}
			else {
				linie.put(towarId, docelowaIloscWLiniiKoszyka);
			}
			if (linie.isEmpty()) {
				rezerwacje.remove(uzytkownik);
			}
			else {
				rezerwacje.put(uzytkownik, linie);
			}
		}
	}

	private int sumaRezerwacjiDlaTowaru(Long towarId) {
		int suma = 0;
		for (Map<Long, Integer> linie : rezerwacje.values()) {
			suma += linie.getOrDefault(towarId, 0);
		}
		return suma;
	}

	@Override
	public void zwolnijRezerwacjeKoszyka(String uzytkownik, Koszyk koszyk) {
		synchronized (rezerwacje) {
			if (koszyk.jestPusty()) {
				rezerwacje.remove(uzytkownik);
				return;
			}
			Map<Long, Integer> linie = rezerwacje.get(uzytkownik);
			if (linie == null || linie.isEmpty()) {
				return;
			}
			Map<Long, Integer> pozostale = new HashMap<>(linie);
			for (PozycjaKoszyka p : koszyk.getPozycje()) {
				pozostale.remove(p.getTowarId());
			}
			if (pozostale.isEmpty()) {
				rezerwacje.remove(uzytkownik);
			}
			else {
				rezerwacje.put(uzytkownik, pozostale);
			}
		}
	}

	@Override
	public void potwierdzPlatnoscIZrealizujSprzedaz(String uzytkownik, Koszyk koszyk, TowarCatalog towarCatalog) {
		for (PozycjaKoszyka p : koszyk.getPozycje()) {
			towarCatalog.zmniejszStanMagazynowy(p.getTowarId(), p.getIlosc());
		}
		zwolnijRezerwacjeKoszyka(uzytkownik, koszyk);
	}
}
