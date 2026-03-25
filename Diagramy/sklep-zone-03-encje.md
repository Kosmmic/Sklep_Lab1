classDiagram

    %% --- STREFA: ENCJE DANYCH ---

    class Towar {

        -Long id

        -String nazwa

        -BigDecimal cena

        -int stanMagazynowy

        -String kategoria

    }

    class Koszyk {

        -BigDecimal sumaCzesciowa

        +dodaj(Towar t, ilosc)

        +usun(Towar t)

        +obliczSume()

    }

    class PozycjaKoszyka {

        -int ilosc

        -BigDecimal cenaWChwiliDodania

    }

    class Zamowienie {

        -Long id

        -Date dataZlozenia

        -String statusZamowienia

        -String metodaPlatnosci

    }

    class PozycjaZamowienia {

        -int ilosc

        -BigDecimal cenaSnapshot

        -BigDecimal podatek

    }

    class Sprzedaz {

        -String nrFaktury

        -BigDecimal kwotaBrutto

        -BigDecimal kwotaNetto

        -Date dataWystawienia

    }

    class Dostawa {

        -String nrKuriera

        -String statusPrzesylki

        -String dostawca

        -Date data dataWystawienia

        -Date dataDostarczenia

        -Date przewidywanaData

    }

    class Zwrot {

        -String uzasadnienie

        -String statusDecyzji

        -Date dataZgloszenia

        -Date data decyzji

        -String komentarzKierownika

    }

    class ZgloszenieProduktowe {
        <<abstract>>
        -Long id
        -Date dataZgloszenia
        -String status
        -String powod
    }

    class Reklamacja {
        -String opisUsterki
        -Date dataDecyzji
        -String uzasadnienieKierownika
    }

    class GwarancjaDostawcy {
        -String wymaganyTryb
        -Date dataDecyzji
        -String uzasadnienieKierownika
    }

    Reklamacja --|> ZgloszenieProduktowe
    GwarancjaDostawcy --|> ZgloszenieProduktowe
