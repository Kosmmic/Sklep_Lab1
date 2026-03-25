classDiagram

    %% --- STREFA: SERWISY I INTERFEJSY ---

    class Autentykacja {

        <<interface>>

        +zaloguj(login, haslo)

        +wyloguj()

        +resetujHaslo(email)

    }

    class ZarzadzanieKontami {

        <<interface>>

        +stworzNoweKonto(Uzytkownik u)

        +modyfikujUprawnienia(id, nowaRola)

        +zablokujDostep(id, powod)

        +usunKonto(id)

    }

    class ZarzadzaniePersonelem {

        <<interface>>

        +zatrudnijPracownika(Pracownik p)

        +zmienUprawnieniaPracownika(id)

        +edytujDanePracownika(Pracownik p)

    }

    class MagazynPubliczny {

        <<interface>>

        +sprawdzDostepnosc(idTowaru)

        +pobierzCene(idTowaru)

        +pobierzOpis(idTowaru)

    }

    class MagazynPelny {

        <<interface>>

        +pobierzPelnyStanMagazynu(idTowaru)

        +zarezerwujTowar(idTowaru, ilosc)

        +aktualizujStanPoDostawie(idTowaru, ilosc)

        +oznaczTowarJakoUszkodzony(idTowaru)

    }

    MagazynPelny --|> MagazynPubliczny

    class ObslugaDostawPracownik {

        <<interface>>

        +zatwierdzOdbiorDostawy(idDostawy)

        +zglosUwagiDoDostawy(idDostawy, opis)

        +sprawdzStatusDostawy(idDostawy)

    }

    class ZarzadzanieLogistykaKierownik {

        <<interface>>

        +zamowDostaweUProducenta(listaTowarow)

        +anulujDostawe(idDostawy)

        +generujRaportLogistyczny()

    }

    ZarzadzanieLogistykaKierownik --|> ObslugaDostawPracownik

    class ProcesZamowieniaKlient {

        <<interface>>

        +utworzZamowienie(Klient k, Koszyk koszyk)

        +obliczKoszt(adres)

        +potwierdzPlatnosc(idZamowienia)

        +anulujZanimWyslane(idZamowienia)

    }

    class ProcesowanieZamowien {

        <<interface>>

        +zatwierdzDoWysylki(idZam)

        +pobierzStatystykiSprzedazy()

        +wymusZmianeStatusu(idZam, status)

    }

    class ZwrotKlient {

        <<interface>>

        +zglosZwrot(idZamowienia, listaPozycji, powod)

        +sprawdzStatusZwrotu(idZwrotu)

    }

    class ZwrotObslugaPracownik {

        <<interface>>

        +weryfikujStanTowaru(idZwrotu, opisStanu)

        +potwierdzOdbiorPaczki(idZwrotu)

        +dodajZdjeciaWeryfikacyjne(idZwrotu, linki)

    }

    class ZwrotDecyzjaKierownik {

        <<interface>>

        +akceptujZwrot(idZwrotu)

        +odrzucZwrot(idZwrotu, uzasadnienie)

        +zlećZwrotSrodkow(idZwrotu)

    }

    ZwrotDecyzjaKierownik --|> ZwrotObslugaPracownik

    class ReklamacjaKlient {
        <<interface>>
        +zglosReklamacje(idZamowienia, listaPozycji, powod, opis)
        +sprawdzStatusReklamacji(idReklamacji)
    }

    class ReklamacjaObslugaPracownik {
        <<interface>>
        +weryfikujStanTowaruReklamowanego(idReklamacji, opisStanu)
        +dodajZdjeciaWeryfikacyjne(idReklamacji, linki)
    }

    class ReklamacjaDecyzjaKierownik {
        <<interface>>
        +akceptujReklamacje(idReklamacji)
        +odrzucReklamacje(idReklamacji, uzasadnienie)
        +zlećZwrotDoDostawcy(idReklamacji)
    }

    ReklamacjaDecyzjaKierownik --|> ReklamacjaObslugaPracownik
