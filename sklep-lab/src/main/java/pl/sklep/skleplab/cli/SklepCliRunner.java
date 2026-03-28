package pl.sklep.skleplab.cli;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import pl.sklep.skleplab.application.KoszykService;
import pl.sklep.skleplab.application.TowarCatalog;
import pl.sklep.skleplab.application.ZamowienieService;
import pl.sklep.skleplab.application.security.ActorRole;
import pl.sklep.skleplab.domain.MetodaPlatnosci;
import pl.sklep.skleplab.domain.Zamowienie;

@Component
@Profile("cli")
public class SklepCliRunner implements CommandLineRunner {

	private final TowarCatalog towarCatalog;
	private final KoszykService koszykService;
	private final ZamowienieService zamowienieService;
	private final CliActorContextProvider actorContextProvider;
	private ActorRole menuAs = null;

	public SklepCliRunner(TowarCatalog towarCatalog, KoszykService koszykService, ZamowienieService zamowienieService,
			CliActorContextProvider actorContextProvider) {
		this.towarCatalog = towarCatalog;
		this.koszykService = koszykService;
		this.zamowienieService = zamowienieService;
		this.actorContextProvider = actorContextProvider;
	}

	@Override
	public void run(String... args) {
		try (Scanner scanner = new Scanner(System.in)) {
			System.out.println("=== Sklep CLI (demo) — wpisz numer opcji ===");
			while (true) {
				wypiszMenu();
				String linia = scanner.nextLine().trim();
				try {
					obsluz(linia, scanner);
				}
				catch (NumberFormatException e) {
					System.out.println("Błąd: oczekiwano liczby.");
				}
				catch (IllegalArgumentException | IllegalStateException e) {
					System.out.println("Błąd: " + e.getMessage());
				}
			}
		}
	}

	private void wypiszMenu() {
		// „Czyszczenie”/separacja ma być natychmiastowa.
		System.out.println();
		var a = actorContextProvider.current();
		ActorRole effectiveRole = (menuAs != null ? menuAs : a.role());
		StringBuilder sb = new StringBuilder();
		sb.append("Aktor: ")
				.append(a.username())
				.append(" / ")
				.append(a.role());
		if (menuAs != null) {
			sb.append(" | menu jako: ").append(menuAs);
		}
		sb.append(System.lineSeparator());

		switch (effectiveRole) {
			case CLIENT -> sb.append("""
					1 — lista towarów
					2 — dodaj do koszyka (id towaru, ilość)
					3 — pokaż mój koszyk
					4 — złóż zamówienie (metoda płatności)
					9 — zmień aktora/rolę
					0 — koniec
					""");
			case EMPLOYEE -> sb.append("""
					1 — lista towarów
					2 — lista zamówień
					3 — zatwierdź do wysyłki (id zamówienia)
					9 — zmień aktora/rolę
					0 — koniec
					""");
			case MANAGER, ADMIN -> sb.append("""
					1 — lista towarów
					2 — lista zamówień
					3 — zatwierdź do wysyłki (id zamówienia)
					4 — przełącz widok menu jako inna rola
					9 — zmień aktora/rolę
					0 — koniec
					""");
		}

		sb.append("> ");
		typewriterPrint(sb.toString(), 1500);
	}

	private static void typewriterPrint(String text, long totalDurationMs) {
		if (text == null || text.isEmpty()) {
			return;
		}
		long perCharDelayNs = TimeUnit.MILLISECONDS.toNanos(totalDurationMs) / Math.max(1, text.length());
		for (int i = 0; i < text.length(); i++) {
			System.out.print(text.charAt(i));
			// flush rzadziej, żeby nie spowalniać bez sensu na Windows/IDE terminalu
			if ((i % 40) == 0 || i == text.length() - 1) {
				System.out.flush();
			}
			if (perCharDelayNs > 0) {
				try {
					TimeUnit.NANOSECONDS.sleep(perCharDelayNs);
				}
				catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					// Jeśli przerwano, dokończ szybko bez animacji.
					if (i < text.length() - 1) {
						System.out.print(text.substring(i + 1));
						System.out.flush();
					}
					return;
				}
			}
		}
	}

	private void obsluz(String linia, Scanner scanner) {
		ActorRole effectiveRole = (menuAs != null ? menuAs : actorContextProvider.current().role());

		if ("0".equals(linia)) {
			System.out.println("Do widzenia.");
			System.exit(0);
		}
		if ("9".equals(linia)) {
			menuAs = null;
			zmienAktora(scanner);
			return;
		}

		switch (effectiveRole) {
			case CLIENT -> obsluzKlient(linia, scanner);
			case EMPLOYEE -> obsluzPracownik(linia, scanner);
			case MANAGER -> obsluzManager(linia, scanner);
			case ADMIN -> obsluzAdmin(linia, scanner);
		}
	}

	private void obsluzKlient(String linia, Scanner scanner) {
		switch (linia) {
			case "1" -> wypiszTowary();
			case "2" -> {
				requireActualRole(ActorRole.CLIENT);
				System.out.print("id towaru: ");
				long tid = Long.parseLong(scanner.nextLine().trim());
				System.out.print("ilość: ");
				int il = Integer.parseInt(scanner.nextLine().trim());
				koszykService.dodajDoKoszyka(tid, il);
				System.out.println("Dodano do koszyka.");
			}
			case "3" -> {
				requireActualRole(ActorRole.CLIENT);
				var k = koszykService.pobierzKoszyk();
				if (k.jestPusty()) {
					System.out.println("(koszyk pusty)");
				}
				else {
					k.getPozycje().forEach(p ->
							System.out.printf("towarId=%d | ilość=%d | cena=%s zł | linia=%s zł%n",
									p.getTowarId(), p.getIlosc(), p.getCenaWChwiliDodania(), p.wartoscLiniowa()));
					System.out.println("Suma: " + k.obliczSume() + " zł");
				}
			}
			case "4" -> {
				requireActualRole(ActorRole.CLIENT);
				MetodaPlatnosci mp = wyborPlatnosci(scanner);
				Zamowienie z = zamowienieService.zlozZamowienie(mp);
				System.out.println("Złożono zamówienie id=" + z.getId() + " | faktura " + z.getSprzedaz().getNrFaktury());
			}
			default -> System.out.println("Nieznana opcja.");
		}
	}

	private void obsluzPracownik(String linia, Scanner scanner) {
		switch (linia) {
			case "1" -> wypiszTowary();
			case "2" -> {
				requireNotClient();
				zamowienieService.listaZamowien().forEach(this::wypiszZamowienie);
			}
			case "3" -> {
				requireNotClient();
				System.out.print("id zamówienia: ");
				long zid = Long.parseLong(scanner.nextLine().trim());
				zamowienieService.zatwierdzDoWysylki(zid);
				System.out.println("Zamówienie oznaczone jako wysłane.");
			}
			default -> System.out.println("Nieznana opcja.");
		}
	}

	private void obsluzManager(String linia, Scanner scanner) {
		switch (linia) {
			case "4" -> {
				requireAnyActualRole(ActorRole.MANAGER, ActorRole.ADMIN);
				przelaczWidokMenu(scanner);
			}
			default -> obsluzPracownik(linia, scanner);
		}
	}

	private void obsluzAdmin(String linia, Scanner scanner) {
		switch (linia) {
			case "4" -> {
				requireAnyActualRole(ActorRole.MANAGER, ActorRole.ADMIN);
				przelaczWidokMenu(scanner);
			}
			default -> obsluzPracownik(linia, scanner);
		}
	}

	private void przelaczWidokMenu(Scanner scanner) {
		System.out.println("Widok menu jako: 0=DOMYŚLNIE 1=KLIENT 2=PRACOWNIK 3=KIEROWNIK 4=ADMIN");
		System.out.print("wybór: ");
		String wybor = scanner.nextLine().trim();
		menuAs = switch (wybor) {
			case "0" -> null;
			case "1" -> ActorRole.CLIENT;
			case "2" -> ActorRole.EMPLOYEE;
			case "3" -> ActorRole.MANAGER;
			case "4" -> ActorRole.ADMIN;
			default -> throw new IllegalArgumentException("Nieprawidłowy wybór.");
		};
		System.out.println(menuAs == null ? "Przywrócono domyślny widok menu." : "Ustawiono widok menu jako: " + menuAs);
	}

	private void wypiszTowary() {
		towarCatalog.findAll().forEach(t ->
				System.out.printf("id=%d | %s | %s zł | stan=%d | %s%n",
						t.getId(), t.getNazwa(), t.getCena(), t.getStanMagazynowy(), t.getKategoria()));
	}

	private void requireActualRole(ActorRole role) {
		ActorRole actual = actorContextProvider.current().role();
		if (actual != role) {
			throw new IllegalStateException("Brak uprawnień dla roli: " + actual);
		}
	}

	private void requireAnyActualRole(ActorRole... roles) {
		ActorRole actual = actorContextProvider.current().role();
		for (ActorRole r : roles) {
			if (actual == r) {
				return;
			}
		}
		throw new IllegalStateException("Brak uprawnień dla roli: " + actual);
	}

	private void requireNotClient() {
		ActorRole actual = actorContextProvider.current().role();
		if (actual == ActorRole.CLIENT) {
			throw new IllegalStateException("Brak uprawnień dla roli: " + actual);
		}
	}

	private void zmienAktora(Scanner scanner) {
		System.out.println("Rola: 1=KLIENT 2=PRACOWNIK 3=KIEROWNIK 4=ADMIN");
		System.out.print("wybór: ");
		ActorRole role = switch (scanner.nextLine().trim()) {
			case "1" -> ActorRole.CLIENT;
			case "2" -> ActorRole.EMPLOYEE;
			case "3" -> ActorRole.MANAGER;
			case "4" -> ActorRole.ADMIN;
			default -> throw new IllegalArgumentException("Nieprawidłowy wybór roli.");
		};
		System.out.print("username (enter=domyślny): ");
		String u = scanner.nextLine().trim();
		if (u.isBlank()) {
			u = switch (role) {
				case CLIENT -> "klient";
				case EMPLOYEE -> "pracownik";
				case MANAGER -> "kierownik";
				case ADMIN -> "admin";
			};
		}
		actorContextProvider.setCurrent(u, role);
		System.out.println("Ustawiono aktora: " + u + " / " + role);
	}

	private void wypiszZamowienie(Zamowienie z) {
		System.out.printf("id=%d | status=%s | płatność=%s | brutto=%s | dostawa=%s%n",
				z.getId(),
				z.getStatusZamowienia(),
				z.getMetodaPlatnosci(),
				z.getSprzedaz().getKwotaBrutto(),
				z.getDostawa().getStatusPrzesylki());
	}

	private static MetodaPlatnosci wyborPlatnosci(Scanner scanner) {
		System.out.println("Metoda: 1=PRZELEW 2=BLIK 3=KARTA 4=GOTÓWKA przy odbiorze");
		System.out.print("wybór: ");
		return switch (scanner.nextLine().trim()) {
			case "1" -> MetodaPlatnosci.PRZELEW;
			case "2" -> MetodaPlatnosci.BLIK;
			case "3" -> MetodaPlatnosci.KARTA;
			case "4" -> MetodaPlatnosci.GOTOWKA_PRZY_ODBIORZE;
			default -> throw new IllegalArgumentException("Nieprawidłowy wybór metody płatności.");
		};
	}
}
