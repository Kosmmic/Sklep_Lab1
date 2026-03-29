package pl.sklep.skleplab.domain.zgloszenie;

/**
 * Minimalne statusy wspólne dla zgłoszeń produktowych:
 * reklamacji oraz gwarancji dostawcy.
 */
public enum StatusZgloszeniaProduktowego {
	ZGLOSZONE,
	WERYFIKOWANE,
	OCZEKUJE_NA_DECYZJE,
	AKCEPTOWANE,
	ODRZUCONA,
	ZLECONO_ZWROT_DO_DOSTAWCY,
}
