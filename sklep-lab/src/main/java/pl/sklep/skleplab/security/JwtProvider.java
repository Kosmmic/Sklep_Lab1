package pl.sklep.skleplab.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.sklep.skleplab.domain.Uzytkownik;
import java.util.Date;

@Component
public class JwtProvider{

    private static final Duration TOKEN_TTL = Duration.ofHours(1);

    @Value("${jwt.secret}")
    private String secretKey;

    private Key signingKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Uzytkownik uzytkownik){
        Instant now = Instant.now();

        return Jwts.builder()
            .setSubject(uzytkownik.getEmail())
            .claim("role", uzytkownik.getRola().name())
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plus(TOKEN_TTL)))
            .signWith(signingKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder()
                .setSigningKey(signingKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public String getEmailFromToken(String token){
        return Jwts.parserBuilder()
            .setSigningKey(signingKey())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }
}