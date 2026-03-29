package pl.sklep.skleplab.domain.zamowienie;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import pl.sklep.skleplab.domain.katalog.Towar;
import pl.sklep.skleplab.domain.koszyk.Koszyk;

class ZamowienieTest {

	@Test
	void pustyKoszyk_nieMoznaZlozyc() {
		assertThatThrownBy(() -> Zamowienie.utworzZKoszyka(1L, new Koszyk(), MetodaPlatnosci.PRZELEW, LocalDate.now()))
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("pusty");
	}

	@Test
	void koszykZKopia_doZamowienia() {
		Towar t = new Towar(1L, "X", new BigDecimal("10.00"), 5, "Kat");
		Koszyk k = new Koszyk();
		k.dodaj(t, 2);
		Zamowienie z = Zamowienie.utworzZKoszyka(7L, k, MetodaPlatnosci.BLIK, LocalDate.of(2026, 3, 24));
		assertThat(z.getId()).isEqualTo(7L);
		assertThat(z.getPozycje()).hasSize(1);
		assertThat(z.getSprzedaz().getKwotaBrutto()).isEqualByComparingTo(new BigDecimal("20.00"));
		assertThat(z.getDostawa().getStatusPrzesylki()).isEqualTo("OCZEKUJE_NA_PAKOWANIE");
	}
}
