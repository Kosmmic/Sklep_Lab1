package pl.sklep.skleplab.security;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
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
    public SecurityFilterChain filterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) ->
                    response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase()))
                .accessDeniedHandler((request, response, accessDeniedException) ->
                    response.sendError(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase())))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/actuator/health", "/api/v1/ping").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/towary").permitAll()
                .requestMatchers("/api/v1/koszyk/**").hasRole("CLIENT")
                .requestMatchers(HttpMethod.POST, "/api/v1/zamowienia").hasRole("CLIENT")
                .requestMatchers(HttpMethod.GET, "/api/v1/zamowienia").hasAnyRole("EMPLOYEE", "MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/v1/zamowienia/*/wysylka").hasAnyRole("EMPLOYEE", "MANAGER", "ADMIN")
                .anyRequest().authenticated())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        String demoHash = passwordEncoder.encode("demo");

        return new InMemoryUserDetailsManager(
            User.withUsername("klient").password(demoHash).roles("CLIENT").build(),
            User.withUsername("pracownik").password(demoHash).roles("EMPLOYEE").build(),
            User.withUsername("kierownik").password(demoHash).roles("MANAGER").build(),
            User.withUsername("admin").password(demoHash).roles("ADMIN").build(),
            User.withUsername("admin@sklep.pl").password(demoHash).roles("ADMIN").build()
        );
    }

}
