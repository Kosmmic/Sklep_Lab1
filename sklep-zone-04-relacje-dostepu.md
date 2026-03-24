classDiagram

    %% --- STREFA: RELACJE DOSTĘPU (kto używa czego) ---
    %% Skrócone klasy – szczegóły w strefach 1–2.

    class Uzytkownik
    class Administrator
    class Kierownik
    class Pracownik
    class Klient

    class Autentykacja
    class ZarzadzanieKontami
    class ZarzadzaniePersonelem
    class ZarzadzanieLogistykaKierownik
    class MagazynPelny
    class MagazynPubliczny
    class ProcesowanieZamowien
    class ObslugaDostawPracownik
    class ZwrotObslugaPracownik
    class ZwrotDecyzjaKierownik
    class ProcesZamowieniaKlient
    class ZwrotKlient
    class ReklamacjaKlient
    class ReklamacjaObslugaPracownik
    class ReklamacjaDecyzjaKierownik

    Uzytkownik --> Autentykacja

    Administrator --> ZarzadzanieKontami
    Administrator --> ZarzadzanieLogistykaKierownik

    Kierownik --> ZarzadzaniePersonelem
    Kierownik --> ZarzadzanieLogistykaKierownik
    Kierownik --> ZwrotDecyzjaKierownik
    Kierownik --> ReklamacjaDecyzjaKierownik

    Pracownik --> ProcesowanieZamowien
    Pracownik --> ObslugaDostawPracownik
    Pracownik --> ZwrotObslugaPracownik
    Pracownik --> ReklamacjaObslugaPracownik
    Pracownik ..> MagazynPelny

    Klient --> ProcesZamowieniaKlient
    Klient --> ZwrotKlient
    Klient --> ReklamacjaKlient
    Klient ..> MagazynPubliczny
