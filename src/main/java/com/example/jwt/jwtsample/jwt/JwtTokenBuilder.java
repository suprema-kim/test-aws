package com.example.jwt.jwtsample.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JwtTokenBuilder {
    private final String HASH_KEY = "abcdefg";

    public String getJwtToken(Map<String, Object> claims) {
        String token = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, HASH_KEY)
                .compact();

        return token;
    }

    public Claims getJwtPayload(String token) {
        Claims claims = Jwts.parser()
                        .setSigningKey(HASH_KEY)
                        .parseClaimsJws(token)
                        .getBody();

        return claims;
    }
}
