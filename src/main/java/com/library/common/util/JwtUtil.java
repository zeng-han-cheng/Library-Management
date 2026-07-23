package com.library.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.common.model.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SecretKey key; private final long expiration;
    public JwtUtil(@Value("${security.jwt.secret}") String secret, @Value("${security.jwt.expiration:86400000}") long expiration) {
        String normalized = secret == null ? "" : secret;
        if (normalized.length() < 32) normalized = "library-management-jwt-secret-2026";
        this.key = Keys.hmacShaKeyFor(normalized.getBytes(StandardCharsets.UTF_8)); this.expiration = expiration;
    }
    public String createToken(LoginUser user) {
        Date now = new Date();
        return Jwts.builder().subject(String.valueOf(user.getId())).claim("user", user).issuedAt(now).expiration(new Date(now.getTime()+expiration)).signWith(key).compact();
    }
    public LoginUser parse(String token) {
        try { Claims claims=Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload(); return objectMapper.convertValue(claims.get("user"), LoginUser.class); }
        catch (Exception e) { return null; }
    }
}
