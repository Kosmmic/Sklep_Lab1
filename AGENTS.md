# Sklep_Lab — kontekst dla agenta

## Cel tego repozytorium
Projekt **sklepu internetowego** (model domenowy + docelowo implementacja w **Javie**). Źródłem prawdy dla architektury obiektowej jest diagram klas w **Mermaid** (`sklep-pelny.md`) oraz pliki strefowe `sklep-zone-*.md`. Agent ma wspierać spójność **kodu z modelem**, sensowną strukturę pakietów oraz aktualną dokumentację — użytkownik miewa z tym trudności; **proponuj aktualizację `README.md`** po większych zmianach w modelu albo w warstwie implementacji, której dotykasz.

Szczegóły modelu i legenda notacji: [README.md](README.md).

## Struktura (orientacja)
- **`sklep-pelny.md`** — pełny diagram klas (role, interfejsy serwisów, encje, relacje dostępu i strukturalne).
- **`sklep-zone-01-role.md` … `sklep-zone-05-relacje-strukturalne.md`** — te same obszary w mniejszych plikach (łatwiejsze równoległe edycje).
- **`sekwencje.md`** — scenariusze czasowe (jeśli rozbudowujesz flow, trzymaj zgodność z diagramem klas).
- **`Sklep.txt`**, **`sklep.md`**, **`sklep.markdown`** — notatki / warianty; nie nadpisuj bez potrzeby — ustal z użytkownikiem, który plik jest „kanoniczny” poza `sklep-pelny.md`.

Docelowo kod Java (gdy pojawi się w repozytorium): trzymaj **mapowanie 1:1** nazw klas/interfejsów z diagramu tam, gdzie to możliwe (polskie nazwy domenowe vs angielskie w kodzie — **jedna konwencja w całym projekcie**; przy wątpliwościach zapytaj lub zaproponuj tabelę nazewnictwa w README).

## Oczekiwania od agenta
1. **Model przed kodem:** przy nowych encjach lub serwisach najpierw zaktualizuj diagram (`sklep-pelny.md` i ewentualnie odpowiedni `sklep-zone-*.md`), potem implementację — albo jawnie zaznacz rozjazd i uzasadnij.
2. **Java:** preferuj wyraźny podział warstw (np. domena / aplikacja / infrastruktura — zgodnie ze stackiem, który użytkownik wybierze). Interfejsy z diagramu (`<<interface>>`) → `interface` w Javie; klasy abstrakcyjne → `abstract class`. Enums i rekordy tam, gdzie upraszczają model (np. status zamówienia).
3. **Spójność z UML:** relacje strukturalne (kompozycja, krotności) powinny znajdować odzwierciedlenie w modelu danych i w kodzie (kolekcje, `Optional` dla `0..1`).
4. **Bezpieczeństwo i domena:** hasła tylko jako hash (`hasloHash`); nie loguj danych wrażliwych. Role (`Administrator`, `Kierownik`, `Pracownik`, `Klient`) — rozdzielenie uprawnień przy wdrażaniu API lub warstwy aplikacji.
5. **Dokumentacja:** po sensownym kawałku pracy zaproponuj krótką aktualizację `README.md` (np. stack Java, struktura modułów, jak uruchomić, stan vs diagram).
6. **Znane niedoskonałości diagramu:** w README wskazano literówki / błędy w atrybutach (`autoryzujDostadiagramwy`, `data dataWystawienia` itd.) — przy implementacji popraw w kodzie i **zsynchronizuj diagram**, żeby źródło prawdy było jedno.
7. Nie usuwać szkieletu dokumentacji (główne pliki `.md` modelu) bez uzgodnienia z użytkownikiem.

## Git
Repozytorium jest pod **Git**; `.gitignore` jest przygotowany pod typowy projekt Java (Maven/Gradle, IDE). Nie commituj artefaktów builda ani plików IDE wymienionych w `.gitignore`.
