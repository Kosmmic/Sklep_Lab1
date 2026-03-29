package pl.sklep.skleplab.domain.katalog;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Towar z katalogu — odpowiada klasie {@code Towar} z diagramu (bez JPA na razie).
 */
public class Towar {

	private final Long id;
	private final String nazwa;
	private final BigDecimal cena;
	private final int stanMagazynowy;
	private final String kategoria;

	public Towar(Long id, String nazwa, BigDecimal cena, int stanMagazynowy, String kategoria) {
		this.id = Objects.requireNonNull(id);
		this.nazwa = Objects.requireNonNull(nazwa);
		this.cena = Objects.requireNonNull(cena);
		this.stanMagazynowy = stanMagazynowy;
		this.kategoria = Objects.requireNonNull(kategoria);
	}

	public Long getId() {
		return id;
	}

	public String getNazwa() {
		return nazwa;
	}

	public BigDecimal getCena() {
		return cena;
	}

	public int getStanMagazynowy() {
		return stanMagazynowy;
	}

	public String getKategoria() {
		return kategoria;
	}
}
