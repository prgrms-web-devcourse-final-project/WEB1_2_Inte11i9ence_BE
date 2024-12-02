package com.prgrmsfinal.skypedia.oauth2.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {  //토큰을 제작하는 클래스

    private SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getOauthId(String token) {

        return parseClaims(token).get("oauthId", String.class);
    }

    public String getRole(String token) {

        return parseClaims(token).get("role", String.class);
    }

    public Boolean isExpired(String token) {

        return parseClaims(token).getExpiration().before(new Date());
    }

    public String createJwt(String oauthId, String role, Long expiredMs) {

        return Jwts.builder()
                .claim("oauthId", oauthId)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

}
