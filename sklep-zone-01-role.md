classDiagram

    %% --- STREFA: ROLE I OSOBY (AKTORZY) ---

    class Uzytkownik {

        <<abstract>>

        -Long id

        -String login

        -String hasloHash

    }

    class Administrator {

        +konfigurujParametrySystemu()

    }

    class Kierownik {

        -String dzial

        +akceptujRaportyFinansowe()

        +autoryzujDostadiagramwy()

    }

    class Pracownik {

        -String numerPracownika

        +przypiszDoStanowiska()

    }

    class Klient {

        -String email

        -String adresDostawy

        -String numerTelefonu

        +zlozZamowienie()

        +wyswietlHistorie()

    }

    Uzytkownik <|-- Administrator

    Uzytkownik <|-- Kierownik

    Uzytkownik <|-- Pracownik

    Uzytkownik <|-- Klient
