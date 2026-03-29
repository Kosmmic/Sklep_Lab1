# Sklep_Lab

Sklep internetowy budowany etapami w **Javie 21 + Spring Boot 3.x**. Zrodlem prawdy dla modelu domenowego jest diagram klas Mermaid (`Diagramy/sklep-pelny.md`), a scenariusze uzytkownika opisuje `Diagramy/sekwencje.md`.

Projekt realizuje podejście **model-first**: najpierw klasy i relacje z diagramu, potem logika zamówień, następnie role i bezpieczeństwo, na końcu zwroty, reklamacje i logistyka.

## Wymagania

- **Java 21** (JDK) — [Eclipse Temurin](https://adoptium.net/temurin/releases/?version=21) lub Oracle JDK
- **Git** — [git-scm.com](https://git-scm.com/download/win)
- Maven nie jest wymagany globalnie — projekt używa **Maven Wrapper** (`mvnw.cmd`)

## Sklad repozytorium

```
Sklep_Lab/
├── Diagramy/            Model UML Mermaid + scenariusze
│   ├── sklep-pelny.md
│   ├── sklep-zone-01-role.md ... sklep-zone-05-relacje-strukturalne.md
│   └── sekwencje.md
├── plany/               Lokalne notatki (ignorowane przez Git)
├── sklep-lab/           Aplikacja Spring Boot (kod + testy)
│   ├── pom.xml
│   └── src/
│       ├── main/java/pl/sklep/skleplab/
│       │   ├── domain/           (katalog, koszyk, zamowienie, …)
│       │   ├── application/      (service, port, security)
│       │   ├── infrastructure/   (memory, sekretarz)
│       │   ├── api/
│       │   ├── cli/
│       │   └── security/
│       └── test/java/pl/sklep/skleplab/
└── README.md
```

## Jak uruchomic

W katalogu `sklep-lab`:

```powershell
.\mvnw.cmd spring-boot:run
```

Aplikacja startuje na `http://localhost:8080`. Szybki test:

- `http://localhost:8080/actuator/health` — powinno zwrócić `{"status":"UP"}`
- `http://localhost:8080/api/v1/ping` — powinno zwrócić `pong`

### Tryb CLI (demo terminalowe, bez serwera HTTP)

Profil `cli` wyłącza osadzony serwer WWW (`spring.main.web-application-type=none`). Uruchomienie z katalogu `sklep-lab`:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=cli"
```

**Ręczny test (konsola):** przy starcie ekran jest czyszczony (`cls` / `clear`), potem wyświetlane jest menu. Tekst menu i odpowiedzi jest pokazywany z efektem „maszyny do pisania” (domyślnie włączony).

- **Klient (`CLIENT`):** `1` — lista towarów (katalog), `2` — **podmenu Koszyk**, `9` — zmiana aktora/roli, `0` — koniec.
- **Podmenu Koszyk:** `1` — dodanie do koszyka (najpierw **stan magazynu** jak w katalogu, potem `id` towaru i ilość), `2` — podgląd koszyka, `3` — złożenie zamówienia, `0` — powrót do menu głównego. Kolejne obszary z wieloma akcjami wokół jednego tematu mają ten sam wzorzec (pętla + `0` = wstecz).
- **Pracownik (`EMPLOYEE`):** `1` — towary, `2` — lista zamówień, `3` — zatwierdzenie wysyłki po `id`, `9` / `0` jak wyżej.
- **Kierownik / admin (`MANAGER`, `ADMIN`):** jak pracownik oraz `4` — przełączenie widoku menu na inną rolę (symulacja).

Logika biznesowa jest wspólna z REST; w CLI nie ma JWT — aktora ustawia `CliActorContextProvider` (loginy/hasła jak w sekcji kont demo).

## Jak uruchomic testy

```powershell
.\mvnw.cmd test
```

## Struktura projektu

```
sklep-lab/src/main/java/pl/sklep/skleplab/
├── domain/              Encje domenowe (bez Springa), pakiety: `katalog`, `koszyk`, `zamowienie`, `zwrot`, `zgloszenie`, `uzytkownicy`
├── application/
│   ├── service/         Serwisy aplikacyjne (`KoszykService`, `ZamowienieService`, …)
│   ├── port/            Interfejsy (porty): `TowarCatalog`, `Magazyn`, repozytoria, `SekretarzZamowien`
│   └── security/        Kontekst aktora (`ActorContext`), role, `Authz`
├── infrastructure/
│   ├── memory/          Adaptery in-memory (katalog, koszyk, zamówienia, zwroty, reklamacje, gwarancje)
│   └── sekretarz/       Zapis i odczyt zamówień przez `ZamowienieRepository` (fasada use case)
├── api/                 Kontrolery REST i obsługa wyjątków HTTP
├── cli/                 Interfejs tekstowy (profil cli)
└── security/            Spring Security — JWT, role (tryb web)
```

**Pakiety `domain` (Java)** — nazwy folderów = fragment pakietu pod `pl.sklep.skleplab.domain`:

| Podfolder   | Przykładowe klasy |
| ----------- | ----------------- |
| `katalog`   | `Towar` |
| `koszyk`    | `Koszyk`, `PozycjaKoszyka` |
| `zamowienie`| `Zamowienie`, `PozycjaZamowienia`, `Sprzedaz`, `Dostawa`, `MetodaPlatnosci`, `StatusZamowienia` |
| `zwrot`     | `Zwrot`, `StatusZwrotu` |
| `zgloszenie`| `ZgloszenieProduktowe`, `Reklamacja`, `GwarancjaDostawcy`, `StatusZgloszeniaProduktowego` |
| `uzytkownicy` | `Uzytkownik`, `Rola`, `Klient`, `Pracownik`, `Kierownik`, `Administrator` |

### Klasy domenowe (mapowanie z diagramu `sklep-pelny.md`)


| Klasa Java          | Odpowiednik w UML                 | Opis                                                                      |
| ------------------- | --------------------------------- | ------------------------------------------------------------------------- |
| `Towar`             | `Towar`                           | Produkt w katalogu (`id`, `nazwa`, `cena`, `stanMagazynowy`, `kategoria`) |
| `Koszyk`            | `Koszyk`                          | Lista pozycji, metody `dodaj()`, `usun()`, `obliczSume()`                 |
| `PozycjaKoszyka`    | `PozycjaKoszyka`                  | Ilość + snapshot ceny w momencie dodania                                  |
| `Zamowienie`        | `Zamowienie`                      | Złożone z koszyka; zawiera pozycje, sprzedaż i dostawę                    |
| `PozycjaZamowienia` | `PozycjaZamowienia`               | Snapshot ceny i podatku per linia zamówienia                              |
| `Sprzedaz`          | `Sprzedaz`                        | Dokument sprzedaży (faktura) powiązany 1:1 z zamówieniem                  |
| `Dostawa`           | `Dostawa`                         | Przesyłka powiązana 1:1 z zamówieniem                                     |
| `StatusZamowienia`  | `statusZamowienia` (String w UML) | Enum: `NOWE`, `OPLACONE`, `W_REALIZACJI`, `WYSLANE`, `ANULOWANE`          |
| `MetodaPlatnosci`   | `metodaPlatnosci` (String w UML)  | Enum: `PRZELEW`, `BLIK`, `KARTA`, `GOTOWKA_PRZY_ODBIORZE`                 |
| `Uzytkownik` (+ `Klient` / `Pracownik` / `Kierownik` / `Administrator`) | `Uzytkownik` i jego specjalizacje | Encja bazowa z `hasloHash` + typ roli (UML role) |
| `Zwrot`             | `Zwrot`                           | Encja zwrotu i zmiany statusu w scenariuszu zwrotu                      |
| `StatusZwrotu`     | —                                 | Enum statusów procesu zwrotu                                              |
| `ZgloszenieProduktowe` (+ `Reklamacja`, `GwarancjaDostawcy`) | `ZgloszenieProduktowe` | Abstrakcja wspólna dla reklamacji i gwarancji                              |
| `StatusZgloszeniaProduktowego` | —                                 | Enum statusów zgłoszeń produktowych                                      |


### Serwisy i repozytoria


| Klasa / interfejs                  | Odpowiednik w UML                                 | Rola                                                    |
| ---------------------------------- | ------------------------------------------------- | ------------------------------------------------------- |
| `TowarCatalog` (interfejs)         | `MagazynPubliczny` (fragment)                     | Katalog towarów i zmiana stanu magazynowego po sprzedaży  |
| `InMemoryTowarCatalog`             | —                                                 | Implementacja w pamięci (przykładowe towary)             |
| `Magazyn` (interfejs)              | —                                                 | Rezerwacje przy koszyku, realizacja sprzedaży po płatności |
| `InMemoryMagazyn`                  | —                                                 | Implementacja w pamięci                                 |
| `KoszykService`                    | `ProcesZamowieniaKlient` (fragment)               | Koszyk per użytkownik, współpraca z `Magazyn`           |
| `ZamowienieService`                | `ProcesZamowieniaKlient` + `ProcesowanieZamowien` | Złożenie zamówienia, lista, wysyłka                     |
| `SekretarzZamowien` (interfejs)    | —                                                 | Trwałość i odczyt zamówień (`ZamowienieRepository`)     |
| `SekretarzZamowienImpl`            | —                                                 | Implementacja w `infrastructure/sekretarz`              |
| `ZamowienieRepository` (interfejs) | —                                                 | Port zamówień (podmiana na pliki lub JPA)               |
| `InMemoryZamowienieRepository`     | —                                                 | Implementacja w pamięci                                 |
| `KoszykRepository` / `InMemoryKoszykRepository` | —                          | Koszyk sesyjny w pamięci                               |
| `ZwrotService`                     | `ZwrotKlient` / `ZwrotObslugaPracownik` / `ZwrotDecyzjaKierownik` (fragment) | Use case procesu zwrotu |
| `ZwrotRepository` (interfejs)     | —                                                 | Port dostępu do encji zwrotu                               |
| `InMemoryZwrotRepository`         | —                                                 | Implementacja: dane w pamięci                           |
| `ReklamacjaService`               | `ReklamacjaKlient` / `ReklamacjaObslugaPracownik` / `ReklamacjaDecyzjaKierownik` (fragment) | Use case procesu reklamacji |
| `ReklamacjaRepository` (interfejs) | —                                                 | Port dostępu do encji reklamacji                       |
| `InMemoryReklamacjaRepository`   | —                                                 | Implementacja: dane w pamięci                           |
| `GwarancjaDostawcyService`        | Analogicznie do procesu reklamacji                 | Use case procesu gwarancji dostawcy |
| `GwarancjaDostawcyRepository` (interfejs) | —                                                 | Port dostępu do encji gwarancji                       |
| `InMemoryGwarancjaDostawcyRepository` | —                                              | Implementacja: dane w pamięci                           |


## Endpointy REST


| Metoda | URL                               | Kto ma dostęp            | Opis                                                        |
| ------ | --------------------------------- | ------------------------ | ----------------------------------------------------------- |
| `GET`  | `/actuator/health`                | wszyscy                  | Status aplikacji                                            |
| `GET`  | `/api/v1/ping`                    | wszyscy                  | Test: zwraca `pong`                                         |
| `GET`  | `/api/v1/towary`                  | wszyscy                  | Lista towarów                                               |
| `GET`  | `/api/v1/koszyk`                  | CLIENT                   | Zawartość koszyka + suma                                    |
| `POST` | `/api/v1/koszyk/pozycje`          | CLIENT                   | Dodanie pozycji — body: `{"towarId":1,"ilosc":2}`           |
| `POST` | `/api/v1/zamowienia`              | CLIENT                   | Złożenie zamówienia — body: `{"metodaPlatnosci":"PRZELEW"}` |
| `GET`  | `/api/v1/zamowienia`              | EMPLOYEE, MANAGER, ADMIN | Lista zamówień                                              |
| `POST` | `/api/v1/zamowienia/{id}/wysylka` | EMPLOYEE, MANAGER, ADMIN | Zmiana statusu na WYSLANE                                   |
| `POST` | `/api/v1/zamowienia/{id}/zwrot`  | CLIENT                   | Zgłoszenie zwrotu                                         |
| `POST` | `/api/v1/zwroty/{id}/weryfikacja` | EMPLOYEE                 | Weryfikacja stanu zwrotu (pracownik)                     |
| `POST` | `/api/v1/zwroty/{id}/odbior`      | EMPLOYEE                 | Potwierdzenie odbioru paczki (pracownik)                |
| `POST` | `/api/v1/zwroty/{id}/akceptacja` | MANAGER, ADMIN           | Decyzja kierownika: akceptacja                           |
| `POST` | `/api/v1/zwroty/{id}/odrzucenie`  | MANAGER, ADMIN          | Decyzja kierownika: odrzucenie                           |
| `POST` | `/api/v1/zwroty/{id}/zwrot-srodkow` | MANAGER, ADMIN       | Realizacja zwrotu środków (demo)                       |
| `POST` | `/api/v1/zamowienia/{id}/reklamacja` | CLIENT                | Zgłoszenie reklamacji                                    |
| `POST` | `/api/v1/reklamacje/{id}/weryfikacja` | EMPLOYEE             | Weryfikacja reklamacji (pracownik)                      |
| `POST` | `/api/v1/reklamacje/{id}/zdjecia` | EMPLOYEE                | Dodanie zdjęć weryfikacyjnych (pracownik)              |
| `POST` | `/api/v1/reklamacje/{id}/akceptacja` | MANAGER, ADMIN       | Decyzja kierownika: akceptacja                           |
| `POST` | `/api/v1/reklamacje/{id}/odrzucenie` | MANAGER, ADMIN       | Decyzja kierownika: odrzucenie                           |
| `POST` | `/api/v1/reklamacje/{id}/zwrot-do-dostawcy` | MANAGER, ADMIN | Zlecenie zwrotu do dostawcy (demo)                      |
| `POST` | `/api/v1/zamowienia/{id}/gwarancja` | CLIENT                | Zgłoszenie gwarancji dostawcy                           |
| `POST` | `/api/v1/gwarancje/{id}/weryfikacja` | EMPLOYEE             | Weryfikacja gwarancji (pracownik)                      |
| `POST` | `/api/v1/gwarancje/{id}/zdjecia` | EMPLOYEE                | Dodanie zdjęć weryfikacyjnych (pracownik)              |
| `POST` | `/api/v1/gwarancje/{id}/akceptacja` | MANAGER, ADMIN       | Decyzja kierownika: akceptacja                           |
| `POST` | `/api/v1/gwarancje/{id}/odrzucenie` | MANAGER, ADMIN       | Decyzja kierownika: odrzucenie                           |
| `POST` | `/api/v1/gwarancje/{id}/zwrot-do-dostawcy` | MANAGER, ADMIN | Zlecenie zwrotu do dostawcy (demo)                      |


## Status warstwy security (stan biezacy)

Warstwa security dziala juz w trybie **JWT (Bearer Token)**.

- endpoint logowania: `POST /api/v1/auth/login`
- token jest wymagany na endpointach chronionych (naglowek `Authorization: Bearer <token>`)
- testy integracyjne API sa przepiete na logowanie JWT
- `JwtProvider` korzysta z aktualnego API JJWT (`Keys.hmacShaKeyFor`, `parserBuilder`)

Przykladowe logowanie:

```json
{"username":"klient","password":"demo"}
```

Przykladowa odpowiedz:

```json
{"token":"<JWT>"}
```

## Konta demo


| Login       | Hasło  | Rola Spring     | Aktor w UML   |
| ----------- | ------ | --------------- | ------------- |
| `klient`    | `demo` | `ROLE_CLIENT`   | Klient        |
| `pracownik` | `demo` | `ROLE_EMPLOYEE` | Pracownik     |
| `kierownik` | `demo` | `ROLE_MANAGER`  | Kierownik     |
| `admin`     | `demo` | `ROLE_ADMIN`    | Administrator |


## Testy


| Klasa testowa                | Pakiet / warstwa | Co sprawdza |
| ---------------------------- | ---------------- | ----------- |
| `SklepLabApplicationTests`   | `pl.sklep.skleplab` | Kontekst Spring startuje |
| `ZamowienieTest`             | `domain.zamowienie` | Zamówienie z koszyka, pusty koszyk |
| `ZwrotTest`                  | `domain.zwrot` | Przejścia statusów zwrotu |
| `ReklamacjaTest`             | `domain.zgloszenie` | Reklamacja: weryfikacja przed decyzją |
| `ZamowienieServiceTest`      | `application.service` | Złożenie zamówienia czyści koszyk, wysyłka |
| `KoszykServiceVisibilityTest` | `application.service` | Koszyk per użytkownik (alice/bob) |
| `ApiSecurityIntegrationTest` | `api` | 401 / 403 / poprawne role |
| `ZwrotSecurityIntegrationTest` | `api` | Ochrona endpointów zwrotów |
| `ReklamacjaSecurityIntegrationTest` | `api` | Ochrona endpointów reklamacji |


## Status builda

- `.\mvnw.cmd test` — BUILD SUCCESS
- **Pakietowanie:** encje domenowe w `domain/{katalog,koszyk,zamowienie,zwrot,zgloszenie,uzytkownicy}`; warstwa aplikacji w `application/service` i `application/port` (porty, serwisy)
- Warstwa **Magazyn** (`Magazyn` + `InMemoryMagazyn`): rezerwacje w koszyku i zmniejszenie stanu po opłaceniu zamówienia
- **Sekretarz zamówień** (`SekretarzZamowien` / `SekretarzZamowienImpl`): delegacja do `ZamowienieRepository`
- Repozytoria i katalog: `infrastructure/memory` (in-memory)
- API: JWT (`JwtFilter`), użytkownicy demo w `SecurityConfig`
- Zwroty, reklamacje, gwarancje: wariant minimalny z repozytoriami w pamięci

Weryfikacja lokalna:

```powershell
.\mvnw.cmd clean test
```

## Co jeszcze nie jest zaimplementowane

- Reklamacje i gwarancje: wersja minimalna (brak pełnego modelowania pozycji i załączników oraz integracji z łańcuchem dostaw)
- Logistyka rozbudowana (`MagazynPelny`, `ObslugaDostawPracownik`, `ZarzadzanieLogistykaKierownik`) — w kodzie jest warstwa `Magazyn` (rezerwacje + stan) i prosta dostawa na zamówieniu
- Zarządzanie kontami i personelem
- Trwały zapis danych (obecnie adaptery **in-memory**; pliki lub baza jako kolejny krok)

## Dokumentacja modelu


| Plik                                                                                | Rola                                                    |
| ----------------------------------------------------------------------------------- | ------------------------------------------------------- |
| `Diagramy/sklep-pelny.md`                                                           | Pelny diagram klas Mermaid (zrodlo prawdy)              |
| `Diagramy/sklep-zone-01-role.md` … `Diagramy/sklep-zone-05-relacje-strukturalne.md` | Wycinki tematyczne                                      |
| `Diagramy/sekwencje.md`                                                             | Scenariusze czasowe (zamowienie, reklamacja, HR, konta) |


Diagram można podejrzeć w Cursor/VS Code z rozszerzeniem Mermaid albo na [Mermaid Live Editor](https://mermaid.live/).