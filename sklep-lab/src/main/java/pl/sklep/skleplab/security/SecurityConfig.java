package pl.sklep.skleplab.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Role jak w diagramie (w Springu prefiks {@code ROLE_} jest domyślny — w kodzie używasz {@code hasRole("CLIENT")}).
 * Aktywne tylko przy aplikacji web (profil {@code cli} bez servletu — ta konfiguracja się nie ładuje).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfig {

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable)
				//.sessionManagment(session ->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) to dodac
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/actuator/health", "/api/v1/ping").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/towary").permitAll()
						.requestMatchers("/api/v1/koszyk/**").hasRole("CLIENT")
						.requestMatchers(HttpMethod.POST, "/api/v1/zamowienia").hasRole("CLIENT")
						.requestMatchers(HttpMethod.GET, "/api/v1/zamowienia").hasAnyRole("EMPLOYEE", "MANAGER", "ADMIN")
						.requestMatchers(HttpMethod.POST, "/api/v1/zamowienia/*/wysylka").hasAnyRole("EMPLOYEE", "MANAGER", "ADMIN")
						.anyRequest().authenticated())
				.httpBasic(Customizer.withDefaults());
		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Konta demo — hasła tylko do nauki; w produkcji: baza + prawdziwy magazyn użytkowników.
	 * Hasło dla wszystkich: {@code demo}
	 */
	@Bean
	UserDetailsService userDetailsService(PasswordEncoder encoder) {
		String hash = encoder.encode("demo");
		return new InMemoryUserDetailsManager(
				User.withUsername("klient").password(hash).roles("CLIENT").build(),
				User.withUsername("pracownik").password(hash).roles("EMPLOYEE").build(),
				User.withUsername("kierownik").password(hash).roles("MANAGER").build(),
				User.withUsername("admin").password(hash).roles("ADMIN").build());
	}
}
