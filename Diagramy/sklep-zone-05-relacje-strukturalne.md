classDiagram

    %% --- STREFA: RELACJE STRUKTURALNE ---
    %% Skrócone klasy – atrybuty w strefie 3.

    class Klient
    class Koszyk
    class PozycjaKoszyka
    class Towar
    class Zamowienie
    class PozycjaZamowienia
    class Sprzedaz
    class Dostawa
    class Zwrot
    class Reklamacja
    class GwarancjaDostawcy
    class Pracownik
    class Kierownik

    Klient "1" -- "1" Koszyk : "posiada aktywny"

    Koszyk "1" *-- "0..*" PozycjaKoszyka : "zawiera elementy"

    PozycjaKoszyka "n" --> "1" Towar : "odnosi się do"

    Klient "1" -- "0..*" Zamowienie : "zleca wykonanie"

    Zamowienie "1" *-- "1..*" PozycjaZamowienia : "składa się z"

    PozycjaZamowienia "n" --> "1" Towar : "referencja towaru"

    Zamowienie "1" -- "1" Sprzedaz : "dokumentowane przez"

    Zamowienie "1" -- "1" Dostawa : "wysyłane jako"

    Zamowienie "1" -- "0..1" Zwrot : "może zostać zwrócone"

    Zamowienie "1" -- "0..*" Reklamacja : "ma zgłoszenia reklamacyjne"
    Zamowienie "1" -- "0..*" GwarancjaDostawcy : "ma zgłoszenia gwarancyjne"

    Reklamacja "0..1" --> "0..1" Zwrot : "może skutkować zwrotem do dostawcy"
    GwarancjaDostawcy "0..1" --> "0..1" Zwrot : "może skutkować zwrotem do dostawcy"

    Reklamacja "n" --> "1" Towar : "dotyczy produktu"
    GwarancjaDostawcy "n" --> "1" Towar : "dotyczy produktu"

    Pracownik "*" -- "*" Zamowienie : "fizycznie pakuje"

    Kierownik "*" -- "*" Zwrot : "rozpatruje i uzasadnia"

    Kierownik "1" -- "*" Dostawa : "nadzoruje łańcuch dostaw"
