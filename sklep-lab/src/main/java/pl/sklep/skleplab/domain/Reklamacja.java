package pl.sklep.skleplab.domain;

import java.time.Instant;
import java.util.Objects;

/**
 * Reklamacja (UML: Reklamacja --|> ZgloszenieProduktowe)
 */
public class Reklamacja extends ZgloszenieProduktowe {

	private final String opisUsterki;

	private Instant dataDecyzji;
	private String uzasadnienieKierownika;

	public Reklamacja(
			Long id,
			Long idZamowienia,
			Instant dataZgloszenia,
			StatusZgloszeniaProduktowego status,
			String powod,
			String opisUsterki) {
		super(id, idZamowienia, dataZgloszenia, status, powod);
		this.opisUsterki = Objects.requireNonNull(opisUsterki);
	}

	public String getOpisUsterki() {
		return opisUsterki;
	}

	public Instant getDataDecyzji() {
		return dataDecyzji;
	}

	public String getUzasadnienieKierownika() {
		return uzasadnienieKierownika;
	}

	// decyzje kierownika (UML: ReklamacjaDecyzjaKierownik)
	public void akceptuj() {
		if (getStatus() != StatusZgloszeniaProduktowego.OCZEKUJE_NA_DECYZJE) {
			throw new IllegalStateException("Reklamacja nie oczekuje na decyzję (status: " + getStatus() + ")");
		}
		this.dataDecyzji = Instant.now();
		this.uzasadnienieKierownika = null;
		setStatus(StatusZgloszeniaProduktowego.AKCEPTOWANE);
	}

	public void odrzuc(String uzasadnienie) {
		if (getStatus() != StatusZgloszeniaProduktowego.OCZEKUJE_NA_DECYZJE) {
			throw new IllegalStateException("Reklamacja nie oczekuje na decyzję (status: " + getStatus() + ")");
		}
		this.dataDecyzji = Instant.now();
		this.uzasadnienieKierownika = Objects.requireNonNull(uzasadnienie);
		setStatus(StatusZgloszeniaProduktowego.ODRZUCONA);
	}

	public void zlecZwrotDoDostawcy() {
		if (getStatus() != StatusZgloszeniaProduktowego.AKCEPTOWANE) {
			// w diagramie to akcja z decyzji, więc przyjmujemy: tylko po akceptacji
			throw new IllegalStateException("Zwrot do dostawcy możliwy tylko po akceptacji (status: " + getStatus() + ")");
		}
		setStatus(StatusZgloszeniaProduktowego.ZLECONO_ZWROT_DO_DOSTAWCY);
	}
}

