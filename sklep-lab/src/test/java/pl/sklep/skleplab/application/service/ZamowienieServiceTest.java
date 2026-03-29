package pl.sklep.skleplab.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import pl.sklep.skleplab.application.port.TowarCatalog;
import pl.sklep.skleplab.domain.zamowienie.MetodaPlatnosci;
import pl.sklep.skleplab.domain.zamowienie.StatusZamowienia;

@SpringBootTest
class ZamowienieServiceTest {

	@Autowired
	private ZamowienieService zamowienieService;

	@Autowired
	private KoszykService koszykService;

	@Autowired
	private TowarCatalog towarCatalog;

	@Test
	void zlozenieZamowienia_czysciKoszyk() {
		setRole("klient", "ROLE_CLIENT");
		koszykService.wyczyscKoszyk();
		koszykService.dodajDoKoszyka(1L, 1);
		var z = zamowienieService.zlozZamowienie(MetodaPlatnosci.PRZELEW);
		assertThat(z.getStatusZamowienia()).isEqualTo(StatusZamowienia.OPLACONE);
		assertThat(koszykService.pobierzKoszyk().jestPusty()).isTrue();
		setRole("pracownik", "ROLE_EMPLOYEE");
		assertThat(zamowienieService.listaZamowien()).anyMatch(o -> o.getId().equals(z.getId()));
	}

	@Test
	void zlozenieZamowienia_zmniejszaStanMagazynowy() {
		int stanPrzed = towarCatalog.findById(1L).orElseThrow().getStanMagazynowy();
		setRole("klient", "ROLE_CLIENT");
		koszykService.wyczyscKoszyk();
		koszykService.dodajDoKoszyka(1L, 1);

		zamowienieService.zlozZamowienie(MetodaPlatnosci.PRZELEW);

		int stanPo = towarCatalog.findById(1L).orElseThrow().getStanMagazynowy();
		assertThat(stanPo).isEqualTo(stanPrzed - 1);
	}

	@Test
	void pustyKoszyk_blad() {
		setRole("klient", "ROLE_CLIENT");
		koszykService.wyczyscKoszyk();
		assertThatThrownBy(() -> zamowienieService.zlozZamowienie(MetodaPlatnosci.BLIK))
				.isInstanceOf(IllegalStateException.class);
	}

	@Test
	void zatwierdzDoWysylki_poOplaceniu() {
		setRole("klient", "ROLE_CLIENT");
		koszykService.wyczyscKoszyk();
		koszykService.dodajDoKoszyka(1L, 1);
		var z = zamowienieService.zlozZamowienie(MetodaPlatnosci.PRZELEW);
		setRole("pracownik", "ROLE_EMPLOYEE");
		zamowienieService.zatwierdzDoWysylki(z.getId());
		var poWysylce = zamowienieService.listaZamowien().stream()
				.filter(x -> x.getId().equals(z.getId()))
				.findFirst()
				.orElseThrow();
		assertThat(poWysylce.getStatusZamowienia()).isEqualTo(StatusZamowienia.WYSLANE);
	}

	private static void setRole(String username, String role) {
		var auth = new UsernamePasswordAuthenticationToken(username, "n/a",
				java.util.List.of(new SimpleGrantedAuthority(role)));
		SecurityContextHolder.getContext().setAuthentication(auth);
	}
}
