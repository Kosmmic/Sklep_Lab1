sequenceDiagram
  participant Klient
  participant Pracownik
  participant Kierownik
  participant Administrator
  participant ProcesZamowieniaKlient
  participant ProcesowanieZamowien
  participant ReklamacjaKlient
  participant ReklamacjaObslugaPracownik
  participant ReklamacjaDecyzjaKierownik
  participant ZwrotObslugaPracownik
  participant ZwrotDecyzjaKierownik
  participant ZarzadzaniePersonelem
  participant ZarzadzanieKontami

  par "Scenariusz A: Zamówienie OK"
    Klient->>ProcesZamowieniaKlient: utworzZamowienie(koszyk)
    Klient->>ProcesZamowieniaKlient: potwierdzPlatnosc(idZamowienia)
    Pracownik->>ProcesowanieZamowien: zatwierdzDoWysylki(idZam)
  and "Scenariusz B: Obiekcja -> Reklamacja -> Zwrot"
    Klient->>ReklamacjaKlient: zglosReklamacje(idZamowienia, listaPozycji, powod, opis)
    Pracownik->>ReklamacjaObslugaPracownik: weryfikujStanTowaruReklamowanego(idReklamacji, opisStanu)
    Kierownik->>ReklamacjaDecyzjaKierownik: akceptujReklamacje(idReklamacji)
    Kierownik->>ReklamacjaDecyzjaKierownik: zlećZwrotDoDostawcy(idReklamacji)
    Pracownik->>ZwrotObslugaPracownik: potwierdzOdbiorPaczki(idZwrotu)
    Kierownik->>ZwrotDecyzjaKierownik: zlećZwrotSrodkow(idZwrotu)
  and "Scenariusz C: HR"
    Kierownik->>ZarzadzaniePersonelem: zatrudnijPracownika(Pracownik)
    Kierownik->>ZarzadzaniePersonelem: zmienUprawnieniaPracownika(idPracownika)
  and "Scenariusz D: Konta"
    Administrator->>ZarzadzanieKontami: zablokujDostep(id, powod)
    Administrator->>ZarzadzanieKontami: usunKonto(id)
  end
