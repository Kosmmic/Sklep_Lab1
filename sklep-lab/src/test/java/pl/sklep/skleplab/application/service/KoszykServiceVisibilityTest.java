package pl.sklep.skleplab.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest
class KoszykServiceVisibilityTest {

	@Autowired
	private KoszykService koszykService;

	@Test
	void koszykJednegoUzytkownikaNieJestWidocznyUInnego() {
		setRole("alice", "ROLE_CLIENT");
		koszykService.wyczyscKoszyk();
		koszykService.dodajDoKoszyka(1L, 1);
		assertThat(koszykService.pobierzKoszyk().jestPusty()).isFalse();

		setRole("bob", "ROLE_CLIENT");
		koszykService.wyczyscKoszyk();
		assertThat(koszykService.pobierzKoszyk().jestPusty()).isTrue();
	}

	private static void setRole(String username, String role) {
		var auth = new UsernamePasswordAuthenticationToken(username, "n/a",
				List.of(new SimpleGrantedAuthority(role)));
		SecurityContextHolder.getContext().setAuthentication(auth);
	}
}
