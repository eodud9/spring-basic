package com.example.spring_basic.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;


@Component
public class JwtTokenProvider {

    @Value("${jwt.secret-key}")
    private String secretKey;

    private final long expirationTime = 1000 * 60 * 60;

    private final long refreshExpiraationTime = 1000L * 60 * 60 * 24 * 14;

    private final SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

    public String createToken(Long userId, String email){
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("type", "ACCESS")
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(Long userId, String email){
        Date now = new Date();
        Date expiration = new Date(now.getTime() + refreshExpiraationTime);

        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("type", "REFRESH")
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    public String getEmail(String token){
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token){
        try{
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch(Exception e){
            return false;
        }
    }

    public String getTokenType(String token){
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("type", String.class);
    }

}
