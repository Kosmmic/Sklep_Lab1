package pl.sklep.skleplab.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Zgloszenie produktowe (UML: abstrakcja dziedziczona przez Reklamacja i GwarancjaDostawcy).
 */
public abstract class ZgloszenieProduktowe {

	private final Long id;
	private final Long idZamowienia;
	private final Instant dataZgloszenia;
	private StatusZgloszeniaProduktowego status;
	private final String powod;

	private String opisStanu;
	private final List<String> linkiZdjecWeryfikacyjnych = new ArrayList<>();

	protected ZgloszenieProduktowe(
			Long id,
			Long idZamowienia,
			Instant dataZgloszenia,
			StatusZgloszeniaProduktowego status,
			String powod) {
		this.id = Objects.requireNonNull(id);
		this.idZamowienia = Objects.requireNonNull(idZamowienia);
		this.dataZgloszenia = Objects.requireNonNull(dataZgloszenia);
		this.status = Objects.requireNonNull(status);
		this.powod = Objects.requireNonNull(powod);
	}

	public Long getId() {
		return id;
	}

	public Long getIdZamowienia() {
		return idZamowienia;
	}

	public Instant getDataZgloszenia() {
		return dataZgloszenia;
	}

	public StatusZgloszeniaProduktowego getStatus() {
		return status;
	}

	protected void setStatus(StatusZgloszeniaProduktowego status) {
		this.status = Objects.requireNonNull(status);
	}

	public String getPowod() {
		return powod;
	}

	public String getOpisStanu() {
		return opisStanu;
	}

	public List<String> getLinkiZdjecWeryfikacyjnych() {
		return Collections.unmodifiableList(linkiZdjecWeryfikacyjnych);
	}

	public void weryfikuj(String opisStanu) {
		this.opisStanu = Objects.requireNonNull(opisStanu);
		setStatus(StatusZgloszeniaProduktowego.OCZEKUJE_NA_DECYZJE);
	}

	public void dodajZdjeciaWeryfikacyjne(List<String> linki) {
		this.linkiZdjecWeryfikacyjnych.addAll(Objects.requireNonNull(linki));
	}
}

