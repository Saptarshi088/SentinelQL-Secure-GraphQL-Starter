package com.saptarshi.DemoInterview.jwt;

import com.saptarshi.DemoInterview.entity.AppUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET;

    @Value("${jwt.token.expiration}")
    private long TOKEN_EXPIRATION_TIME;

    public String generateToken(AppUser appUser) {

        return Jwts.builder()
                .subject(appUser.getEmail())
                .claim("role", appUser.getRole().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + Duration.ofMinutes(TOKEN_EXPIRATION_TIME).toMillis()))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .compact();
    }

    private <T> T getClaims(String token, Function<Claims, T> claimsResolver) {
        var claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsResolver.apply(claims);
    }

    public Boolean isTokenExpired(String token) {
        Date expiration = getClaims(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    public String extractEmail(String token) {
        return getClaims(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return getClaims(token, claims -> claims.get("role", String.class));
    }

}
