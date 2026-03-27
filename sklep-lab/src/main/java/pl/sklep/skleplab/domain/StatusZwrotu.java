package pl.sklep.skleplab.domain;

/**
 * Minimalny model statusów zwrotu dla ścieżki z diagramu UML:
 * zgłoszenie -> weryfikacja (pracownik) -> decyzja (kierownik) -> realizacja.
 */
public enum StatusZwrotu {
	ZGLOSZONY,
	ZWERYFIKOWANY,
	OCZEKUJE_NA_DECYZJE,
	AKCEPTOWANY,
	ODRZUCONY,
	ZREALIZOWANY
}

