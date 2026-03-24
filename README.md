# Sklep_Lab — diagram klas (UML / Mermaid)

Ten katalog zawiera model obiektowy sklepu internetowego w postaci diagramu klas w notacji **Mermaid** (`sklep-pelny.md`). Poniżej: jak z niego korzystać oraz co oznaczają poszczególne części modelu.

## Jak wyświetlić diagram

- **Cursor / VS Code**: podgląd pliku Markdown z blokiem `classDiagram` (np. rozszerzenie *Markdown Preview Mermaid Support* lub wbudowany podgląd, jeśli obsługuje Mermaid).
- **GitHub / GitLab**: renderowanie Mermaid w plikach `.md` (w zależności od ustawień repozytorium).
- **Ręcznie**: skopiuj zawartość bloku `classDiagram …` do [Mermaid Live Editor](https://mermaid.live/).

Główny, scalony widok: **`sklep-pelny.md`**. Pozostałe pliki `sklep-zone-*.md` to wycinki tematyczne tego samego modelu (role, serwisy, encje, relacje dostępu, relacje strukturalne).

## Zawartość katalogu (skrót)

| Plik | Rola |
|------|------|
| `sklep-pelny.md` | Pełny diagram klas (wszystkie sekcje w jednym pliku) |
| `sklep-zone-01-role.md` … `sklep-zone-05-relacje-strukturalne.md` | Fragmenty według stref modelu |
| `sklep.md`, `sklep.markdown`, `Sklep.txt`, `sekwencje.md` | Inne notatki / warianty dokumentacji |

## Struktura modelu w `sklep-pelny.md`

### 1. Role i osoby (aktorzy biznesowi)

Abstrakcyjna klasa bazowa **`Uzytkownik`** grupuje wspólne dane tożsamości (`id`, `login`, `hasloHash`). Dziedziczą po niej:

- **`Administrator`** — konfiguracja parametrów systemu.
- **`Kierownik`** — m.in. akceptacja raportów finansowych, autoryzacja dostaw (w diagramie występuje literówka w nazwie metody: `autoryzujDostadiagramwy`).
- **`Pracownik`** — numer pracownika, przypisanie do stanowiska.
- **`Klient`** — dane kontaktowe i dostawy, składanie zamówień, historia.

Relacja dziedziczenia: `Uzytkownik <|--` podklasy (specjalizacja).

### 2. Serwisy i interfejsy

Interfejsy opisują **kontrakty** operacji (logika aplikacji), a nie magazyn danych. Wybrane grupy:

| Interfejs | Znaczenie |
|-----------|-----------|
| `Autentykacja` | Logowanie, wylogowanie, reset hasła |
| `ZarzadzanieKontami` | CRUD kont, uprawnienia, blokady (perspektywa administratora) |
| `ZarzadzaniePersonelem` | Zatrudnienie, uprawnienia i dane pracowników |
| `MagazynPubliczny` | Dostęp klienta: dostępność, cena, opis |
| `MagazynPelny` | Pełny stan, rezerwacje, dostawy, uszkodzenia — **rozszerza** `MagazynPubliczny` (`--|>`) |
| `ObslugaDostawPracownik` | Odbiór dostaw, uwagi, status |
| `ZarzadzanieLogistykaKierownik` | Zamówienia u producenta, anulowanie, raporty — **rozszerza** obsługę dostaw pracownika |
| `ProcesZamowieniaKlient` | Koszyk → zamówienie, koszt, płatność, anulowanie przed wysyłką |
| `ProcesowanieZamowien` | Zatwierdzenie do wysyłki, statystyki, zmiana statusu (perspektywa pracownika) |
| `ZwrotKlient` / `ZwrotObslugaPracownik` / `ZwrotDecyzjaKierownik` | Ścieżka zwrotu: klient → weryfikacja → decyzja i zwrot środków (kierownik rozszerza interfejs pracownika) |
| `ReklamacjaKlient` / `ReklamacjaObslugaPracownik` / `ReklamacjaDecyzjaKierownik` | Analogiczna warstwa dla reklamacji |

Stereotyp `<<interface>>` w Mermaid odpowiada interfejsowi UML.

### 3. Encje danych

Klasy opisujące **stan przechowywany** (towary, koszyk, zamówienia, logistyka, zwroty, zgłoszenia):

- **`Towar`**, **`Koszyk`**, **`PozycjaKoszyka`** — katalog i koszyk (pozycja wskazuje na towar).
- **`Zamowienie`**, **`PozycjaZamowienia`** — zamówienie z pozycjami i snapshotem ceny/podatku.
- **`Sprzedaz`**, **`Dostawa`**, **`Zwrot`** — faktura, przesyłka, ewentualny zwrot zamówienia.
- **`ZgloszenieProduktowe`** (abstrakcyjne) — wspólna baza dla **`Reklamacja`** i **`GwarancjaDostawcy`** (specjalizacje).

W diagramie w klasach `Dostawa` i `Zwrot` występują nazwy atrybutów z odstępem (`data dataWystawienia`, `data decyzji`) — przy implementacji warto je ujednolicić do poprawnej składni (np. `dataWystawienia`, `dataDecyzji`).

### 4. Relacje dostępu (kto z czego korzysta)

Strzałki **`-->`** (zależność / użycie): która rola **wywołuje** który interfejs.

- Administrator: konta, logistyka (jak kierownik).
- Kierownik: personel, logistyka, zwroty (decyzje), reklamacje (decyzje).
- Pracownik: procesowanie zamówień, dostawy, zwroty (obsługa), reklamacje (obsługa); zależność od **`MagazynPelny`** (`..>` — zwykle słabsza zależność w UML).
- Klient: proces zamówienia, zwroty (zgłoszenia), reklamacje (zgłoszenia); dostęp do **`MagazynPubliczny`** (`..>`).

### 5. Relacje strukturalne (asocjacje i kompozycja)

- **Kompozycja** (`*--`): koszyk zawiera pozycje; zamówienie składa się z pozycji (minimum jedna).
- **Asocjacje** z krotnościami (`"1"`, `"0..*"`, `"1..*"` itd.): klient–koszyk, klient–zamówienia, pozycje → towar, zamówienie–sprzedaż/dostawa/zwrot/reklamacje/gwarancje.
- **Reklamacja / GwarancjaDostawcy** mogą wiązać się opcjonalnie ze **zwrotem** do dostawcy i zawsze odnoszą się do **towaru** (krotności `n` → `1`).
- **Wielu-do-wielu**: pracownik pakujący ↔ zamówienia; kierownik ↔ zwroty (rozpatrywanie).
- **Kierownik** ↔ **Dostawa** (`1` — `*`): nadzór nad łańcuchem dostaw.

## Legenda notacji (skrót)

| Zapis w Mermaid | Znaczenie |
|-----------------|-----------|
| `<<abstract>>` / `<<interface>>` | Klasa abstrakcyjna / interfejs |
| `A <|-- B` | B dziedziczy po A |
| `A --|> B` | B implementuje / rozszerza interfejs A |
| `A --> B` | Zależność lub kierunek użycia |
| `A ..> B` | Słabsza zależność (np. użycie) |
| `A *-- B` | Kompozycja (część nie istnieje bez całości) |
| `A -- B` z etykietą i liczbami | Asocjacja z krotnością i opisem roli |

---

*README opisuje model z `sklep-pelny.md`; przy refaktoryzacji diagramu zaktualizuj odpowiednio ten plik.*
