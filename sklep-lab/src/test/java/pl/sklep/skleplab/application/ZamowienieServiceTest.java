package pl.sklep.skleplab.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pl.sklep.skleplab.domain.MetodaPlatnosci;
import pl.sklep.skleplab.domain.StatusZamowienia;

@SpringBootTest
class ZamowienieServiceTest {

	@Autowired
	private ZamowienieService zamowienieService;

	@Autowired
	private KoszykService koszykService;

	@Test
	void zlozenieZamowienia_czysciKoszyk() {
		koszykService.wyczyscKoszyk();
		koszykService.dodajDoKoszyka(1L, 1);
		var z = zamowienieService.zlozZamowienie(MetodaPlatnosci.PRZELEW);
		assertThat(z.getStatusZamowienia()).isEqualTo(StatusZamowienia.OPLACONE);
		assertThat(koszykService.pobierzKoszyk().jestPusty()).isTrue();
		assertThat(zamowienieService.listaZamowien()).anyMatch(o -> o.getId().equals(z.getId()));
	}

	@Test
	void pustyKoszyk_blad() {
		koszykService.wyczyscKoszyk();
		assertThatThrownBy(() -> zamowienieService.zlozZamowienie(MetodaPlatnosci.BLIK))
				.isInstanceOf(IllegalStateException.class);
	}

	@Test
	void zatwierdzDoWysylki_poOplaceniu() {
		koszykService.wyczyscKoszyk();
		koszykService.dodajDoKoszyka(1L, 1);
		var z = zamowienieService.zlozZamowienie(MetodaPlatnosci.PRZELEW);
		zamowienieService.zatwierdzDoWysylki(z.getId());
		var poWysylce = zamowienieService.listaZamowien().stream()
				.filter(x -> x.getId().equals(z.getId()))
				.findFirst()
				.orElseThrow();
		assertThat(poWysylce.getStatusZamowienia()).isEqualTo(StatusZamowienia.WYSLANE);
	}
}
