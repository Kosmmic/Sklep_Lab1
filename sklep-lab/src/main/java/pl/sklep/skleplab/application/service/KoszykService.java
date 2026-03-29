package pl.sklep.skleplab.application.service;

import org.springframework.stereotype.Service;

import pl.sklep.skleplab.application.port.KoszykRepository;
import pl.sklep.skleplab.application.port.Magazyn;
import pl.sklep.skleplab.application.port.TowarCatalog;
import pl.sklep.skleplab.application.security.ActorContextProvider;
import pl.sklep.skleplab.domain.katalog.Towar;
import pl.sklep.skleplab.domain.koszyk.Koszyk;
import pl.sklep.skleplab.domain.koszyk.PozycjaKoszyka;

@Service
public class KoszykService {

	private final TowarCatalog towarCatalog;
	private final KoszykRepository koszykRepository;
	private final Magazyn magazyn;
	private final ActorContextProvider actorContextProvider;

	public KoszykService(TowarCatalog towarCatalog, KoszykRepository koszykRepository, Magazyn magazyn,
			ActorContextProvider actorContextProvider) {
		this.towarCatalog = towarCatalog;
		this.koszykRepository = koszykRepository;
		this.magazyn = magazyn;
		this.actorContextProvider = actorContextProvider;
	}

	public Koszyk pobierzKoszyk() {
		String username = actorContextProvider.current().username();
		return koszykRepository.pobierzDlaUzytkownika(username);
	}

	public void dodajDoKoszyka(Long towarId, int ilosc) {
		Towar towar = towarCatalog.findById(towarId)
				.orElseThrow(() -> new IllegalArgumentException("Nieznany towar: " + towarId));
		String username = actorContextProvider.current().username();
		Koszyk koszyk = koszykRepository.pobierzDlaUzytkownika(username);
		int staraIlosc = koszyk.getPozycje().stream()
				.filter(p -> p.getTowarId().equals(towarId))
				.mapToInt(PozycjaKoszyka::getIlosc)
				.findFirst()
				.orElse(0);
		int nowaIlosc = staraIlosc + ilosc;
		magazyn.zarezerwuj(username, towarId, nowaIlosc, towar.getStanMagazynowy());
		koszyk.dodaj(towar, ilosc);
		koszykRepository.zapisz(username, koszyk);
	}

	public void wyczyscKoszyk() {
		String username = actorContextProvider.current().username();
		Koszyk koszyk = koszykRepository.pobierzDlaUzytkownika(username);
		magazyn.zwolnijRezerwacjeKoszyka(username, koszyk);
		koszyk.wyczysc();
		koszykRepository.zapisz(username, koszyk);
	}
}
