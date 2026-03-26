package pl.sklep.skleplab.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.sklep.skleplab.domain.Uzytkownik;
import java.util.Date;

@Component
public class JwtProvider{

    @Value("${jwt.secret}")
    private String secretKey;

    public String generateToken(Uzytkownik uzytkownik){
        long teraz = System.currentTimeMillis();

        return Jwts.builder()
        .setSubject(uzytkownik.getEmail())
        .claim("role", uzytkownik.getRola().name())
        .setIssuedAt(new Date(teraz))
        .setExpiration(new Date(teraz + 3600000))
        .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
        .compact();
    }

}