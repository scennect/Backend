package com.example.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private SecretKey secretkey;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        secretkey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUserNameFromToken(String token) {
        return Jwts.parser().verifyWith(secretkey).build().parseClaimsJws(token).getPayload().get("username", String.class);
    }

    public String getRoleFromToken(String token) {
        return Jwts.parser().verifyWith(secretkey).build().parseClaimsJws(token).getPayload().get("role", String.class);
    }

    public Boolean isExpired(String token) {
        return  Jwts.parser().verifyWith(secretkey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String createJWT(String username, String role, Long expireTime) {
        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(secretkey)
                .compact();
    }

}
