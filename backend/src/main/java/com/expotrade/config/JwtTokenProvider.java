package com.expotrade.config;

import com.expotrade.domain.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;

    public JwtTokenProvider(
            @Value("${jwt.secret:expotrade-default-secret-key-change-in-production-min-256-bits!!}") String secret,
            @Value("${jwt.access-token-validity:3600000}") long accessTokenValidity,
            @Value("${jwt.refresh-token-validity:86400000}") long refreshTokenValidity) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    public String generateToken(User user) {
        Date now = new Date();
        return Jwts.builder().subject(user.id().toString())
                .claim("username", user.username()).claim("roles", user.roles())
                .issuedAt(now).expiration(new Date(now.getTime() + accessTokenValidity))
                .signWith(secretKey).compact();
    }

    public String generateRefreshToken(User user) {
        Date now = new Date();
        return Jwts.builder().subject(user.id().toString())
                .issuedAt(now).expiration(new Date(now.getTime() + refreshTokenValidity))
                .signWith(secretKey).compact();
    }

    public String getUserIdFromToken(String token) {
        return parseClaims(token).getPayload().getSubject();
    }

    public boolean validateToken(String token) {
        try { parseClaims(token); return true; }
        catch (JwtException | IllegalArgumentException e) { return false; }
    }

    private Jws<Claims> parseClaims(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
    }
}
