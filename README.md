# Sklep_Lab

Sklep internetowy budowany etapami w **Javie 21 + Spring Boot 3.x**. Źródłem prawdy dla modelu domenowego jest diagram klas w notacji Mermaid (`sklep-pelny.md`), a scenariusze użytkownika opisuje `sekwencje.md`.

Projekt realizuje podejście **model-first**: najpierw klasy i relacje z diagramu, potem logika zamówień, następnie role i bezpieczeństwo, na końcu zwroty, reklamacje i logistyka.

## Wymagania

- **Java 21** (JDK) — [Eclipse Temurin](https://adoptium.net/temurin/releases/?version=21) lub Oracle JDK
- **Git** — [git-scm.com](https://git-scm.com/download/win)
- Maven nie jest wymagany globalnie — projekt używa **Maven Wrapper** (`mvnw.cmd`)

## Jak uruchomić

W katalogu `sklep-lab`:

```powershell
.\mvnw.cmd spring-boot:run
```

Aplikacja startuje na `http://localhost:8080`. Szybki test:
- `http://localhost:8080/actuator/health` — powinno zwrócić `{"status":"UP"}`
- `http://localhost:8080/api/v1/ping` — powinno zwrócić `pong`

### Tryb CLI (demo terminałowe, bez serwera HTTP)

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=cli"
```

Menu w konsoli pozwala przeglądać towary, dodawać do koszyka, składać zamówienia i zatwierdzać wysyłkę — bez przeglądarki i bez Postmana.

## Jak uruchomić testy

```powershell
.\mvnw.cmd test
```

## Struktura projektu

```
sklep-lab/src/main/java/pl/sklep/skleplab/
├── domain/           Encje i reguły biznesowe (bez Springa, bez bazy)
├── application/      Serwisy (use case) i interfejsy repozytoriów
├── infrastructure/   Implementacje repozytoriów (na razie w pamięci)
├── api/              Kontrolery REST i obsługa wyjątków HTTP
├── cli/              Interfejs tekstowy (profil cli)
└── security/         Spring Security — role i konfiguracja (tylko tryb web)
```

### Klasy domenowe (mapowanie z diagramu `sklep-pelny.md`)

| Klasa Java | Odpowiednik w UML | Opis |
|------------|-------------------|------|
| `Towar` | `Towar` | Produkt w katalogu (`id`, `nazwa`, `cena`, `stanMagazynowy`, `kategoria`) |
| `Koszyk` | `Koszyk` | Lista pozycji, metody `dodaj()`, `usun()`, `obliczSume()` |
| `PozycjaKoszyka` | `PozycjaKoszyka` | Ilość + snapshot ceny w momencie dodania |
| `Zamowienie` | `Zamowienie` | Złożone z koszyka; zawiera pozycje, sprzedaż i dostawę |
| `PozycjaZamowienia` | `PozycjaZamowienia` | Snapshot ceny i podatku per linia zamówienia |
| `Sprzedaz` | `Sprzedaz` | Dokument sprzedaży (faktura) powiązany 1:1 z zamówieniem |
| `Dostawa` | `Dostawa` | Przesyłka powiązana 1:1 z zamówieniem |
| `StatusZamowienia` | `statusZamowienia` (String w UML) | Enum: `NOWE`, `OPLACONE`, `W_REALIZACJI`, `WYSLANE`, `ANULOWANE` |
| `MetodaPlatnosci` | `metodaPlatnosci` (String w UML) | Enum: `PRZELEW`, `BLIK`, `KARTA`, `GOTOWKA_PRZY_ODBIORZE` |

### Serwisy i repozytoria

| Klasa / interfejs | Odpowiednik w UML | Rola |
|-------------------|-------------------|------|
| `TowarCatalog` (interfejs) | `MagazynPubliczny` (fragment) | Dostęp do katalogu towarów |
| `InMemoryTowarCatalog` | — | Implementacja: dane w pamięci (3 przykładowe towary) |
| `KoszykService` | `ProcesZamowieniaKlient` (fragment) | Dodawanie do koszyka, pobieranie, czyszczenie |
| `ZamowienieService` | `ProcesZamowieniaKlient` + `ProcesowanieZamowien` | Złożenie zamówienia z koszyka, zatwierdzenie do wysyłki |
| `ZamowienieRepository` (interfejs) | — | CRUD zamówień (port pod podmianę na pliki/JPA) |
| `InMemoryZamowienieRepository` | — | Implementacja: dane w pamięci |

## Endpointy REST

| Metoda | URL | Kto ma dostęp | Opis |
|--------|-----|---------------|------|
| `GET` | `/actuator/health` | wszyscy | Status aplikacji |
| `GET` | `/api/v1/ping` | wszyscy | Test: zwraca `pong` |
| `GET` | `/api/v1/towary` | wszyscy | Lista towarów |
| `GET` | `/api/v1/koszyk` | CLIENT | Zawartość koszyka + suma |
| `POST` | `/api/v1/koszyk/pozycje` | CLIENT | Dodanie pozycji — body: `{"towarId":1,"ilosc":2}` |
| `POST` | `/api/v1/zamowienia` | CLIENT | Złożenie zamówienia — body: `{"metodaPlatnosci":"PRZELEW"}` |
| `GET` | `/api/v1/zamowienia` | EMPLOYEE, MANAGER, ADMIN | Lista zamówień |
| `POST` | `/api/v1/zamowienia/{id}/wysylka` | EMPLOYEE, MANAGER, ADMIN | Zmiana statusu na WYSLANE |

Uwierzytelnianie: **HTTP Basic**. W Postmanie: zakładka Authorization, typ Basic Auth.

## Konta demo

| Login | Hasło | Rola Spring | Aktor w UML |
|-------|-------|-------------|-------------|
| `klient` | `demo` | `ROLE_CLIENT` | Klient |
| `pracownik` | `demo` | `ROLE_EMPLOYEE` | Pracownik |
| `kierownik` | `demo` | `ROLE_MANAGER` | Kierownik |
| `admin` | `demo` | `ROLE_ADMIN` | Administrator |

## Testy

| Klasa testowa | Co sprawdza |
|---------------|-------------|
| `SklepLabApplicationTests` | Czy kontekst Spring wstaje poprawnie |
| `ZamowienieTest` | Logika domeny: tworzenie zamówienia z koszyka, walidacja pustego koszyka |
| `ZamowienieServiceTest` | Serwis: złożenie zamówienia czyści koszyk, zatwierdzenie do wysyłki zmienia status |
| `ApiSecurityIntegrationTest` | Ochrona endpointów: 401 bez logowania, 403 przy złej roli, 200/201 z poprawną rolą |

## Znane literówki z diagramu (poprawione w kodzie)

| W diagramie | W kodzie Java |
|-------------|---------------|
| `autoryzujDostadiagramwy()` | `autoryzujDostawy()` (do poprawy w UML) |
| `data dataWystawienia` w `Dostawa` | `dataNadania` |
| `data decyzji` w `Zwrot` | `dataDecyzji` (do implementacji) |
| `nrKuriera` | `numerPrzesylki` |
| `statusZamowienia` jako String | enum `StatusZamowienia` |
| `metodaPlatnosci` jako String | enum `MetodaPlatnosci` |

## Co jeszcze nie jest zaimplementowane

- Encje użytkowników (`Uzytkownik`, `Klient`, `Pracownik`, `Kierownik`, `Administrator`) jako obiekty domenowe z `hasloHash`
- Zwroty (`Zwrot`, `ZwrotKlient`, `ZwrotObslugaPracownik`, `ZwrotDecyzjaKierownik`)
- Reklamacje (`Reklamacja`, `GwarancjaDostawcy`, `ZgloszenieProduktowe`)
- Logistyka i magazyn (`MagazynPelny`, `ObslugaDostawPracownik`, `ZarzadzanieLogistykaKierownik`)
- Zarządzanie kontami i personelem
- Trwały zapis danych (pliki JSON lub baza danych)
- JWT zamiast HTTP Basic
- Osobne koszyki per użytkownik

## Dokumentacja modelu

| Plik | Rola |
|------|------|
| `sklep-pelny.md` | Pełny diagram klas Mermaid (źródło prawdy) |
| `sklep-zone-01-role.md` … `sklep-zone-05-relacje-strukturalne.md` | Wycinki tematyczne |
| `sekwencje.md` | Scenariusze czasowe (zamówienie, reklamacja, HR, konta) |

Diagram można podejrzeć w Cursor/VS Code z rozszerzeniem Mermaid albo na [Mermaid Live Editor](https://mermaid.live/).
