package pl.sklep.skleplab.domain.zgloszenie;

import java.time.Instant;
import java.util.Objects;

/**
 * GwarancjaDostawcy (UML: --|> ZgloszenieProduktowe)
 */
public class GwarancjaDostawcy extends ZgloszenieProduktowe {

	private final String wymaganyTryb;
	private Instant dataDecyzji;
	private String uzasadnienieKierownika;

	public GwarancjaDostawcy(
			Long id,
			Long idZamowienia,
			Instant dataZgloszenia,
			StatusZgloszeniaProduktowego status,
			String powod,
			String wymaganyTryb) {
		super(id, idZamowienia, dataZgloszenia, status, powod);
		this.wymaganyTryb = Objects.requireNonNull(wymaganyTryb);
	}

	public String getWymaganyTryb() {
		return wymaganyTryb;
	}

	public Instant getDataDecyzji() {
		return dataDecyzji;
	}

	public String getUzasadnienieKierownika() {
		return uzasadnienieKierownika;
	}

	public void akceptuj() {
		if (getStatus() != StatusZgloszeniaProduktowego.OCZEKUJE_NA_DECYZJE) {
			throw new IllegalStateException("Gwarancja nie oczekuje na decyzję (status: " + getStatus() + ")");
		}
		this.dataDecyzji = Instant.now();
		this.uzasadnienieKierownika = null;
		setStatus(StatusZgloszeniaProduktowego.AKCEPTOWANE);
	}

	public void odrzuc(String uzasadnienie) {
		if (getStatus() != StatusZgloszeniaProduktowego.OCZEKUJE_NA_DECYZJE) {
			throw new IllegalStateException("Gwarancja nie oczekuje na decyzję (status: " + getStatus() + ")");
		}
		this.dataDecyzji = Instant.now();
		this.uzasadnienieKierownika = Objects.requireNonNull(uzasadnienie);
		setStatus(StatusZgloszeniaProduktowego.ODRZUCONA);
	}

	public void zlecZwrotDoDostawcy() {
		if (getStatus() != StatusZgloszeniaProduktowego.AKCEPTOWANE) {
			throw new IllegalStateException("Zwrot do dostawcy możliwy tylko po akceptacji (status: " + getStatus() + ")");
		}
		setStatus(StatusZgloszeniaProduktowego.ZLECONO_ZWROT_DO_DOSTAWCY);
	}
}
