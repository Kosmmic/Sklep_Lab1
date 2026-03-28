package pl.sklep.skleplab.application;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import pl.sklep.skleplab.application.security.ActorContextProvider;
import pl.sklep.skleplab.application.security.ActorRole;
import pl.sklep.skleplab.application.security.Authz;
import pl.sklep.skleplab.domain.MetodaPlatnosci;
import pl.sklep.skleplab.domain.StatusZamowienia;
import pl.sklep.skleplab.domain.Zamowienie;

@Service
public class ZamowienieService {

	private final ActorContextProvider actorContextProvider;
	private final KoszykService koszykService;
	private final TowarCatalog towarCatalog;
	private final Magazyn magazyn;
	private final SekretarzZamowien sekretarzZamowien;

	public ZamowienieService(ActorContextProvider actorContextProvider, KoszykService koszykService,
			TowarCatalog towarCatalog, Magazyn magazyn, SekretarzZamowien sekretarzZamowien) {
		this.actorContextProvider = actorContextProvider;
		this.koszykService = koszykService;
		this.towarCatalog = towarCatalog;
		this.magazyn = magazyn;
		this.sekretarzZamowien = sekretarzZamowien;
	}

	public Zamowienie zlozZamowienie(MetodaPlatnosci metodaPlatnosci) {
		Authz.requireAnyRole(actorContextProvider.current(), ActorRole.CLIENT);
		String username = actorContextProvider.current().username();
		var koszyk = koszykService.pobierzKoszyk();
		if (koszyk.jestPusty()) {
			throw new IllegalStateException("Koszyk jest pusty — nie można złożyć zamówienia");
		}
		magazyn.potwierdzPlatnoscIZrealizujSprzedaz(username, koszyk, towarCatalog);
		long id = sekretarzZamowien.nastepnyIdZamowienia();
		Zamowienie z = Zamowienie.utworzZKoszyka(id, koszyk, metodaPlatnosci, LocalDate.now());
		z.setStatusZamowienia(StatusZamowienia.OPLACONE);
		sekretarzZamowien.zakolejkujPoPlatnosci(username, z);
		koszykService.wyczyscKoszyk();
		return z;
	}

	public List<Zamowienie> listaZamowien() {
		Authz.requireAnyRole(actorContextProvider.current(), ActorRole.EMPLOYEE, ActorRole.MANAGER, ActorRole.ADMIN);
		return sekretarzZamowien.pobierzWszystkieZamowienia();
	}

	public void zatwierdzDoWysylki(long zamowienieId) {
		Authz.requireAnyRole(actorContextProvider.current(), ActorRole.EMPLOYEE, ActorRole.MANAGER, ActorRole.ADMIN);
		Zamowienie z = sekretarzZamowien.znajdzZamowienie(zamowienieId)
				.orElseThrow(() -> new IllegalArgumentException("Brak zamówienia o id: " + zamowienieId));
		if (z.getStatusZamowienia() != StatusZamowienia.OPLACONE) {
			throw new IllegalArgumentException(
					"Wysyłka możliwa tylko dla zamówień opłaconych; aktualny status: " + z.getStatusZamowienia());
		}
		z.setStatusZamowienia(StatusZamowienia.WYSLANE);
		z.getDostawa().setStatusPrzesylki("WYSŁANO");
		sekretarzZamowien.zapiszZamowienieWBackendzie(z);
	}
}
