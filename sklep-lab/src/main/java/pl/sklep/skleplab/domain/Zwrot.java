package pl.sklep.skleplab.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Zwrot zamówienia (UML: {@code Zwrot}) — na start uproszczony,
 * ale z atrybutami i przejściami w stylu scenariusza z diagramu.
 */
public class Zwrot {

	private final Long id;
	private final Long idZamowienia;
	private final String powod;

	private StatusZwrotu statusDecyzji;
	private final Instant dataZgloszenia;
	private Instant dataDecyzji;
	private String komentarzKierownika;

	private String opisStanu;
	private boolean odbiorPaczkiPotwierdzony;
	private final List<String> linkiZdjecWeryfikacyjnych = new ArrayList<>();

	public Zwrot(Long id, Long idZamowienia, String powod, Instant dataZgloszenia) {
		this.id = Objects.requireNonNull(id);
		this.idZamowienia = Objects.requireNonNull(idZamowienia);
		this.powod = Objects.requireNonNull(powod);
		this.dataZgloszenia = Objects.requireNonNull(dataZgloszenia);
		this.statusDecyzji = StatusZwrotu.ZGLOSZONY;
	}

	public Long getId() {
		return id;
	}

	public Long getIdZamowienia() {
		return idZamowienia;
	}

	public String getPowod() {
		return powod;
	}

	public StatusZwrotu getStatusDecyzji() {
		return statusDecyzji;
	}

	public Instant getDataZgloszenia() {
		return dataZgloszenia;
	}

	public Instant getDataDecyzji() {
		return dataDecyzji;
	}

	public String getKomentarzKierownika() {
		return komentarzKierownika;
	}

	public String getOpisStanu() {
		return opisStanu;
	}

	public boolean isOdbiorPaczkiPotwierdzony() {
		return odbiorPaczkiPotwierdzony;
	}

	public List<String> getLinkiZdjecWeryfikacyjnych() {
		return Collections.unmodifiableList(linkiZdjecWeryfikacyjnych);
	}

	// --- przejścia w scenariuszu ---

	public void weryfikujStanTowaru(String opisStanu) {
		this.opisStanu = Objects.requireNonNull(opisStanu);
		this.statusDecyzji = StatusZwrotu.ZWERYFIKOWANY;
	}

	public void potwierdzOdbiorPaczki() {
		this.odbiorPaczkiPotwierdzony = true;
		this.statusDecyzji = StatusZwrotu.OCZEKUJE_NA_DECYZJE;
	}

	public void dodajZdjeciaWeryfikacyjne(List<String> linki) {
		this.linkiZdjecWeryfikacyjnych.addAll(Objects.requireNonNull(linki));
	}

	public void akceptujZwrot() {
		if (this.statusDecyzji != StatusZwrotu.OCZEKUJE_NA_DECYZJE) {
			throw new IllegalStateException("Zwrot nie oczekuje na decyzję (aktualnie: " + statusDecyzji + ")");
		}
		this.dataDecyzji = Instant.now();
		this.komentarzKierownika = null;
		this.statusDecyzji = StatusZwrotu.AKCEPTOWANY;
	}

	public void odrzucZwrot(String uzasadnienie) {
		if (this.statusDecyzji != StatusZwrotu.OCZEKUJE_NA_DECYZJE) {
			throw new IllegalStateException("Zwrot nie oczekuje na decyzję (aktualnie: " + statusDecyzji + ")");
		}
		this.dataDecyzji = Instant.now();
		this.komentarzKierownika = Objects.requireNonNull(uzasadnienie);
		this.statusDecyzji = StatusZwrotu.ODRZUCONY;
	}

	public void zlećZwrotSrodkow() {
		if (this.statusDecyzji != StatusZwrotu.AKCEPTOWANY) {
			throw new IllegalStateException("Zwrot środków możliwy tylko po akceptacji (aktualnie: " + statusDecyzji + ")");
		}
		this.statusDecyzji = StatusZwrotu.ZREALIZOWANY;
	}
}

