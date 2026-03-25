package pl.sklep.skleplab.cli;

import java.util.Scanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import pl.sklep.skleplab.application.KoszykService;
import pl.sklep.skleplab.application.TowarCatalog;
import pl.sklep.skleplab.application.ZamowienieService;
import pl.sklep.skleplab.domain.MetodaPlatnosci;
import pl.sklep.skleplab.domain.Zamowienie;

/**
 * Interfejs tekstowy — te same serwisy co REST API, inne „wejście” (diagram nie przewiduje HTTP).
 * Uruchomienie: {@code .\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=cli}
 */
@Component
@Profile("cli")
public class SklepCliRunner implements CommandLineRunner {

	private final TowarCatalog towarCatalog;
	private final KoszykService koszykService;
	private final ZamowienieService zamowienieService;

	public SklepCliRunner(TowarCatalog towarCatalog, KoszykService koszykService, ZamowienieService zamowienieService) {
		this.towarCatalog = towarCatalog;
		this.koszykService = koszykService;
		this.zamowienieService = zamowienieService;
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

	private static void wypiszMenu() {
		System.out.println();
		System.out.println("1 — lista towarów");
		System.out.println("2 — dodaj do koszyka (id towaru, ilość)");
		System.out.println("3 — pokaż koszyk");
		System.out.println("4 — złóż zamówienie (metoda płatności)");
		System.out.println("5 — lista zamówień");
		System.out.println("6 — pracownik: zatwierdź do wysyłki (id zamówienia)");
		System.out.println("0 — koniec");
		System.out.print("> ");
	}

	private void obsluz(String linia, Scanner scanner) {
		switch (linia) {
			case "0" -> {
				System.out.println("Do widzenia.");
				System.exit(0);
			}
			case "1" -> towarCatalog.findAll().forEach(t ->
					System.out.printf("id=%d | %s | %s zł | stan=%d | %s%n",
							t.getId(), t.getNazwa(), t.getCena(), t.getStanMagazynowy(), t.getKategoria()));
			case "2" -> {
				System.out.print("id towaru: ");
				long tid = Long.parseLong(scanner.nextLine().trim());
				System.out.print("ilość: ");
				int il = Integer.parseInt(scanner.nextLine().trim());
				koszykService.dodajDoKoszyka(tid, il);
				System.out.println("Dodano do koszyka.");
			}
			case "3" -> {
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
				MetodaPlatnosci mp = wyborPlatnosci(scanner);
				Zamowienie z = zamowienieService.zlozZamowienie(mp);
				System.out.println("Złożono zamówienie id=" + z.getId() + " | faktura " + z.getSprzedaz().getNrFaktury());
			}
			case "5" -> zamowienieService.listaZamowien().forEach(this::wypiszZamowienie);
			case "6" -> {
				System.out.print("id zamówienia: ");
				long zid = Long.parseLong(scanner.nextLine().trim());
				zamowienieService.zatwierdzDoWysylki(zid);
				System.out.println("Zamówienie oznaczone jako wysłane.");
			}
			default -> System.out.println("Nieznana opcja.");
		}
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
