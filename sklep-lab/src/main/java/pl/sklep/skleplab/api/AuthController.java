package pl.sklep.skleplab.api;

import java.util.Map;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.sklep.skleplab.domain.Administrator;
import pl.sklep.skleplab.domain.Kierownik;
import pl.sklep.skleplab.domain.Klient;
import pl.sklep.skleplab.domain.Pracownik;
import pl.sklep.skleplab.domain.Rola;
import pl.sklep.skleplab.domain.Uzytkownik;
import pl.sklep.skleplab.security.JwtProvider;

@RestController
@RequestMapping("/api/v1/auth")
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class AuthController {

    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(JwtProvider jwtProvider, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> request) {
        String email = request.getOrDefault("email", request.get("username"));
        String password = request.get("password");

        if (email == null || password == null) {
            throw new BadCredentialsException("Brak loginu lub hasla");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Bledne dane logowania");
        }

        Rola rola = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .map(authority -> authority.replace("ROLE_", ""))
            .findFirst()
            .map(Rola::valueOf)
            .orElseThrow(() -> new IllegalStateException("Brak roli uzytkownika"));

        // mapowanie Spring Security -> encja domenowa (hasloHash już jest zwracane jako hash z UserDetails)
        String hasloHash = userDetails.getPassword();
        Uzytkownik user = switch (rola) {
            case CLIENT -> new Klient(userDetails.getUsername(), hasloHash);
            case EMPLOYEE -> new Pracownik(userDetails.getUsername(), hasloHash, "demo-employee");
            case MANAGER -> new Kierownik(userDetails.getUsername(), hasloHash, "demo-department");
            case ADMIN -> new Administrator(userDetails.getUsername(), hasloHash);
        };
        String token = jwtProvider.generateToken(user);
        return Map.of("token", token);
    }
}
