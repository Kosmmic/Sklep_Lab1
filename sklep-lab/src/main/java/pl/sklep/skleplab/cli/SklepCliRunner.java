package pl.sklep.skleplab.cli;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import pl.sklep.skleplab.application.port.TowarCatalog;
import pl.sklep.skleplab.application.service.KoszykService;
import pl.sklep.skleplab.application.service.ZamowienieService;
import pl.sklep.skleplab.application.security.ActorRole;
import pl.sklep.skleplab.domain.zamowienie.MetodaPlatnosci;
import pl.sklep.skleplab.domain.zamowienie.Zamowienie;

/**
 * CLI: główne menu + podmenu kontekstowe (np. Koszyk).
 * Kolejne obszary z kilkoma akcjami wokół jednego tematu — ten sam wzorzec: {@code while} + opcja „0” = powrót.
 */
@Component
@Profile("cli")
public class SklepCliRunner implements CommandLineRunner {

	private static final long TW_MENU_MS = 1500L;
	private static final long TW_REPLY_MS = 1000L;
	private static final long TW_PROMPT_MS = 600L;

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
		clearConsole();
		try (Scanner scanner = new Scanner(System.in)) {
			twLine("=== Sklep CLI (demo) — wpisz numer opcji ===", TW_REPLY_MS);
			while (true) {
				wypiszMenu();
				String linia = scanner.nextLine().trim();
				try {
					obsluz(linia, scanner);
				}
				catch (NumberFormatException e) {
					twLine("Błąd: oczekiwano liczby.", TW_REPLY_MS);
				}
				catch (IllegalArgumentException | IllegalStateException e) {
					twLine("Błąd: " + e.getMessage(), TW_REPLY_MS);
				}
			}
		}
	}

	/** Czyści ekran terminala przed startem (Windows: {@code cls}, pozostałe: {@code clear}; awaria → sekwencja ANSI). */
	private static void clearConsole() {
		try {
			String os = System.getProperty("os.name", "").toLowerCase();
			ProcessBuilder pb = os.contains("win")
					? new ProcessBuilder("cmd", "/c", "cls")
					: new ProcessBuilder("clear");
			pb.inheritIO();
			Process p = pb.start();
			p.waitFor();
		}
		catch (IOException | InterruptedException e) {
			Thread.currentThread().interrupt();
			System.out.print("\033[H\033[2J");
			System.out.flush();
		}
	}

	private void wypiszMenu() {
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
					1 — lista towarów (katalog)
					2 — Koszyk (podmenu: dodaj, podgląd, zamówienie)
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
		typewriterPrint(sb.toString(), TW_MENU_MS);
	}

	private void twLine(String line) {
		twLine(line, TW_REPLY_MS);
	}

	private void twLine(String line, long totalDurationMs) {
		if (line == null) {
			return;
		}
		typewriterPrint(line + System.lineSeparator(), totalDurationMs);
	}

	private void twPrompt(String prompt) {
		if (prompt == null || prompt.isEmpty()) {
			return;
		}
		typewriterPrint(prompt, TW_PROMPT_MS);
	}

	private static void typewriterPrint(String text, long totalDurationMs) {
		if (text == null || text.isEmpty()) {
			return;
		}
		long perCharDelayNs = TimeUnit.MILLISECONDS.toNanos(totalDurationMs) / Math.max(1, text.length());
		for (int i = 0; i < text.length(); i++) {
			System.out.print(text.charAt(i));
			if ((i % 40) == 0 || i == text.length() - 1) {
				System.out.flush();
			}
			if (perCharDelayNs > 0) {
				try {
					TimeUnit.NANOSECONDS.sleep(perCharDelayNs);
				}
				catch (InterruptedException e) {
					Thread.currentThread().interrupt();
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
			twLine("Do widzenia.", TW_REPLY_MS);
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
				podmenuKoszyk(scanner);
			}
			default -> twLine("Nieznana opcja.");
		}
	}

	/**
	 * Podmenu Koszyk: dodawanie (najpierw stan magazynu), podgląd koszyka, składanie zamówienia.
	 * Wzór na kolejne podmenu — osobna pętla {@code while}, opcja {@code 0} wraca do menu głównego.
	 */
	private void podmenuKoszyk(Scanner scanner) {
		while (true) {
			StringBuilder sb = new StringBuilder();
			sb.append(System.lineSeparator());
			sb.append("── Podmenu: Koszyk ──").append(System.lineSeparator());
			sb.append("1 — dodaj do koszyka (najpierw stan magazynu)").append(System.lineSeparator());
			sb.append("2 — pokaż mój koszyk").append(System.lineSeparator());
			sb.append("3 — złóż zamówienie").append(System.lineSeparator());
			sb.append("0 — wróć do menu głównego").append(System.lineSeparator());
			sb.append("koszyk> ");
			typewriterPrint(sb.toString(), TW_MENU_MS);

			String linia = scanner.nextLine().trim();
			try {
				if ("0".equals(linia)) {
					twLine("Powrót do menu głównego.");
					return;
				}
				switch (linia) {
					case "1" -> dodajDoKoszykaZPodglademMagazynu(scanner);
					case "2" -> pokazMojKoszyk();
					case "3" -> zlozZamowienieZPodmenu(scanner);
					default -> twLine("Nieznana opcja w podmenu Koszyk.");
				}
			}
			catch (NumberFormatException e) {
				twLine("Błąd: oczekiwano liczby.", TW_REPLY_MS);
			}
			catch (IllegalArgumentException | IllegalStateException e) {
				twLine("Błąd: " + e.getMessage(), TW_REPLY_MS);
			}
		}
	}

	private void dodajDoKoszykaZPodglademMagazynu(Scanner scanner) {
		twLine("Stan magazynu — wybierz id towaru i ilość:");
		wypiszTowary();
		twPrompt("id towaru: ");
		long tid = Long.parseLong(scanner.nextLine().trim());
		twPrompt("ilość: ");
		int il = Integer.parseInt(scanner.nextLine().trim());
		koszykService.dodajDoKoszyka(tid, il);
		twLine("Dodano do koszyka.");
	}

	private void pokazMojKoszyk() {
		var k = koszykService.pobierzKoszyk();
		if (k.jestPusty()) {
			twLine("(koszyk pusty)");
		}
		else {
			StringBuilder sb = new StringBuilder();
			k.getPozycje().forEach(p ->
					sb.append(String.format("towarId=%d | ilość=%d | cena=%s zł | linia=%s zł%n",
							p.getTowarId(), p.getIlosc(), p.getCenaWChwiliDodania(), p.wartoscLiniowa())));
			sb.append("Suma: ").append(k.obliczSume()).append(" zł");
			typewriterPrint(sb.toString() + System.lineSeparator(), TW_REPLY_MS);
		}
	}

	private void zlozZamowienieZPodmenu(Scanner scanner) {
		MetodaPlatnosci mp = wyborPlatnosci(scanner);
		Zamowienie z = zamowienieService.zlozZamowienie(mp);
		twLine("Złożono zamówienie id=" + z.getId() + " | faktura " + z.getSprzedaz().getNrFaktury());
	}

	private void obsluzPracownik(String linia, Scanner scanner) {
		switch (linia) {
			case "1" -> wypiszTowary();
			case "2" -> {
				requireNotClient();
				StringBuilder sb = new StringBuilder();
				zamowienieService.listaZamowien().forEach(z ->
						sb.append(String.format("id=%d | status=%s | płatność=%s | brutto=%s | dostawa=%s%n",
								z.getId(),
								z.getStatusZamowienia(),
								z.getMetodaPlatnosci(),
								z.getSprzedaz().getKwotaBrutto(),
								z.getDostawa().getStatusPrzesylki())));
				if (sb.isEmpty()) {
					twLine("(brak zamówień)");
				}
				else {
					typewriterPrint(sb.toString(), TW_REPLY_MS);
				}
			}
			case "3" -> {
				requireNotClient();
				twPrompt("id zamówienia: ");
				long zid = Long.parseLong(scanner.nextLine().trim());
				zamowienieService.zatwierdzDoWysylki(zid);
				twLine("Zamówienie oznaczone jako wysłane.");
			}
			default -> twLine("Nieznana opcja.");
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
		twLine("Widok menu jako: 0=DOMYŚLNIE 1=KLIENT 2=PRACOWNIK 3=KIEROWNIK 4=ADMIN");
		twPrompt("wybór: ");
		String wybor = scanner.nextLine().trim();
		menuAs = switch (wybor) {
			case "0" -> null;
			case "1" -> ActorRole.CLIENT;
			case "2" -> ActorRole.EMPLOYEE;
			case "3" -> ActorRole.MANAGER;
			case "4" -> ActorRole.ADMIN;
			default -> throw new IllegalArgumentException("Nieprawidłowy wybór.");
		};
		twLine(menuAs == null ? "Przywrócono domyślny widok menu." : "Ustawiono widok menu jako: " + menuAs);
	}

	private void wypiszTowary() {
		StringBuilder sb = new StringBuilder();
		towarCatalog.findAll().forEach(t ->
				sb.append(String.format("id=%d | %s | %s zł | stan=%d | %s%n",
						t.getId(), t.getNazwa(), t.getCena(), t.getStanMagazynowy(), t.getKategoria())));
		if (sb.isEmpty()) {
			twLine("(brak towarów w katalogu)");
		}
		else {
			typewriterPrint(sb.toString(), TW_REPLY_MS);
		}
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
		twLine("Rola: 1=KLIENT 2=PRACOWNIK 3=KIEROWNIK 4=ADMIN");
		twPrompt("wybór: ");
		ActorRole role = switch (scanner.nextLine().trim()) {
			case "1" -> ActorRole.CLIENT;
			case "2" -> ActorRole.EMPLOYEE;
			case "3" -> ActorRole.MANAGER;
			case "4" -> ActorRole.ADMIN;
			default -> throw new IllegalArgumentException("Nieprawidłowy wybór roli.");
		};
		twPrompt("username (enter=domyślny): ");
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
		twLine("Ustawiono aktora: " + u + " / " + role);
	}

	private MetodaPlatnosci wyborPlatnosci(Scanner scanner) {
		twLine("Metoda: 1=PRZELEW 2=BLIK 3=KARTA 4=GOTÓWKA przy odbiorze");
		twPrompt("wybór: ");
		return switch (scanner.nextLine().trim()) {
			case "1" -> MetodaPlatnosci.PRZELEW;
			case "2" -> MetodaPlatnosci.BLIK;
			case "3" -> MetodaPlatnosci.KARTA;
			case "4" -> MetodaPlatnosci.GOTOWKA_PRZY_ODBIORZE;
			default -> throw new IllegalArgumentException("Nieprawidłowy wybór metody płatności.");
		};
	}
}
