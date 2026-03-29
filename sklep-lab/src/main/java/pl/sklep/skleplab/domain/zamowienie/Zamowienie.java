package pl.sklep.skleplab.domain.zamowienie;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import pl.sklep.skleplab.domain.koszyk.Koszyk;
import pl.sklep.skleplab.domain.koszyk.PozycjaKoszyka;

public class Zamowienie {

	private final Long id;
	private final Instant dataZlozenia;
	private StatusZamowienia statusZamowienia;
	private final MetodaPlatnosci metodaPlatnosci;
	private final List<PozycjaZamowienia> pozycje;
	private final Sprzedaz sprzedaz;
	private final Dostawa dostawa;

	public Zamowienie(
			Long id,
			Instant dataZlozenia,
			StatusZamowienia statusZamowienia,
			MetodaPlatnosci metodaPlatnosci,
			List<PozycjaZamowienia> pozycje,
			Sprzedaz sprzedaz,
			Dostawa dostawa) {
		this.id = Objects.requireNonNull(id);
		this.dataZlozenia = Objects.requireNonNull(dataZlozenia);
		this.statusZamowienia = Objects.requireNonNull(statusZamowienia);
		this.metodaPlatnosci = Objects.requireNonNull(metodaPlatnosci);
		this.pozycje = List.copyOf(pozycje);
		this.sprzedaz = Objects.requireNonNull(sprzedaz);
		this.dostawa = Objects.requireNonNull(dostawa);
	}

	public static Zamowienie utworzZKoszyka(
			Long id,
			Koszyk koszyk,
			MetodaPlatnosci metodaPlatnosci,
			LocalDate dzisiaj) {
		Objects.requireNonNull(koszyk);
		if (koszyk.jestPusty()) {
			throw new IllegalStateException("Koszyk jest pusty — nie można złożyć zamówienia");
		}
		List<PozycjaZamowienia> linie = new ArrayList<>();
		for (PozycjaKoszyka p : koszyk.getPozycje()) {
			linie.add(PozycjaZamowienia.zKoszyka(p));
		}
		BigDecimal brutto = linie.stream()
				.map(PozycjaZamowienia::wartoscBrutto)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		String nrFaktury = "FV/" + id + "/" + dzisiaj.getYear();
		Sprzedaz sprzedaz = new Sprzedaz(nrFaktury, brutto, brutto, dzisiaj);
		Dostawa dostawa = new Dostawa(
				null,
				"OCZEKUJE_NA_PAKOWANIE",
				"DemoKurier",
				dzisiaj,
				null,
				dzisiaj.plusDays(3));
		return new Zamowienie(
				id,
				Instant.now(),
				StatusZamowienia.NOWE,
				metodaPlatnosci,
				linie,
				sprzedaz,
				dostawa);
	}

	public Long getId() {
		return id;
	}

	public Instant getDataZlozenia() {
		return dataZlozenia;
	}

	public StatusZamowienia getStatusZamowienia() {
		return statusZamowienia;
	}

	public void setStatusZamowienia(StatusZamowienia statusZamowienia) {
		this.statusZamowienia = Objects.requireNonNull(statusZamowienia);
	}

	public MetodaPlatnosci getMetodaPlatnosci() {
		return metodaPlatnosci;
	}

	public List<PozycjaZamowienia> getPozycje() {
		return Collections.unmodifiableList(pozycje);
	}

	public Sprzedaz getSprzedaz() {
		return sprzedaz;
	}

	public Dostawa getDostawa() {
		return dostawa;
	}
}
