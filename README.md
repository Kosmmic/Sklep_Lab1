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
├── sklep-lab/           Aplikacja Spring Boot (kod + testy)
│   ├── pom.xml
│   └── src/
│       ├── main/java/pl/sklep/skleplab/
│       │   ├── domain/
│       │   ├── application/
│       │   ├── infrastructure/
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

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=cli"
```

Menu w konsoli pozwala przeglądać towary, dodawać do koszyka, składać zamówienia i zatwierdzać wysyłkę — bez przeglądarki i bez Postmana.

## Jak uruchomic testy

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
| `TowarCatalog` (interfejs)         | `MagazynPubliczny` (fragment)                     | Dostęp do katalogu towarów                              |
| `InMemoryTowarCatalog`             | —                                                 | Implementacja: dane w pamięci (3 przykładowe towary)    |
| `KoszykService`                    | `ProcesZamowieniaKlient` (fragment)               | Dodawanie do koszyka, pobieranie, czyszczenie           |
| `ZamowienieService`                | `ProcesZamowieniaKlient` + `ProcesowanieZamowien` | Złożenie zamówienia z koszyka, zatwierdzenie do wysyłki |
| `ZamowienieRepository` (interfejs) | —                                                 | CRUD zamówień (port pod podmianę na pliki/JPA)          |
| `InMemoryZamowienieRepository`     | —                                                 | Implementacja: dane w pamięci                           |
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


| Klasa testowa                | Co sprawdza                                                                        |
| ---------------------------- | ---------------------------------------------------------------------------------- |
| `SklepLabApplicationTests`   | Czy kontekst Spring wstaje poprawnie                                               |
| `ZamowienieTest`             | Logika domeny: tworzenie zamówienia z koszyka, walidacja pustego koszyka           |
| `ZamowienieServiceTest`      | Serwis: złożenie zamówienia czyści koszyk, zatwierdzenie do wysyłki zmienia status |
| `ApiSecurityIntegrationTest` | Ochrona endpointów: 401 bez logowania, 403 przy złej roli, 200/201 z poprawną rolą |


## Status builda

Aktualny stan po poprawkach:

- `.\mvnw.cmd test` przechodzi poprawnie (BUILD SUCCESS)
- usunieto zaleznosc od Lomboka z `AuthController` i `JwtFilter`
- naprawiono literowke w `JwtFilter` (`userDetails`)
- usunieto `httpBasic`; autoryzacja API opiera sie o JWT + `JwtFilter`
- skonfigurowano uzytkownikow demo w `SecurityConfig` (`klient`, `pracownik`, `kierownik`, `admin`, `admin@sklep.pl`)
- dodano encje domenowe użytkowników (`Uzytkownik` jako abstrakcja + `Klient/Pracownik/Kierownik/Administrator`) oraz tokenowanie z wykorzystaniem `hasloHash`
- dodano podstawowy flow zwrotu (`ZwrotController` + `ZwrotService`) wraz z autoryzacją
- dodano reklamacje i gwarancje (`ReklamacjaController`, `GwarancjaDostawcyController`) wraz z serwisami domenowymi i testami (wariant minimalny, in-memory)

Weryfikacja lokalna:

```powershell
.\mvnw.cmd clean test
```

## Co jeszcze nie jest zaimplementowane

- Reklamacje i gwarancje: wersja minimalna (brak pełnego modelowania pozycji i załączników w zgłoszeniach oraz brak pełnej integracji z dalszym łańcuchem dostaw/zwrotów)
- Logistyka i magazyn (`MagazynPelny`, `ObslugaDostawPracownik`, `ZarzadzanieLogistykaKierownik`)
- Zarządzanie kontami i personelem
- Trwały zapis danych (pliki JSON lub baza danych)
- Osobne koszyki per użytkownik

## Dokumentacja modelu


| Plik                                                                                | Rola                                                    |
| ----------------------------------------------------------------------------------- | ------------------------------------------------------- |
| `Diagramy/sklep-pelny.md`                                                           | Pelny diagram klas Mermaid (zrodlo prawdy)              |
| `Diagramy/sklep-zone-01-role.md` … `Diagramy/sklep-zone-05-relacje-strukturalne.md` | Wycinki tematyczne                                      |
| `Diagramy/sekwencje.md`                                                             | Scenariusze czasowe (zamowienie, reklamacja, HR, konta) |


Diagram można podejrzeć w Cursor/VS Code z rozszerzeniem Mermaid albo na [Mermaid Live Editor](https://mermaid.live/).