package com.portfolio.aips.project.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtils {

    private final SecretKey secretKey;


    public final int JWT_EXPIRED_TIME;



    public JwtUtils(@Value("${spring.jwt.secret}") String secret, @Value("${spring.jwt.access-token-expiration}") int JWT_EXPIRED_TIME) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.JWT_EXPIRED_TIME = JWT_EXPIRED_TIME;
    }

    public Object getJWTExpiredTime(Object type)
    {
        if(type == Instant.class) {
            return Instant.now().plusSeconds(JWT_EXPIRED_TIME);
        }
        else{
            return JWT_EXPIRED_TIME;
        }
    }



    public String getPrincipalName(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("principalName", String.class);
    }

    public String getAccessToken(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("accessToken", String.class);
    }


    public String getProvider(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("provider", String.class);
    }

    public Date getExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration();
    }








    public String createJwt(String principalName, String provider, String accessToken, Instant expiresAt) {

        return Jwts.builder()
                .claim("principalName", principalName)
                .claim("provider", provider)
                .claim("accessToken", accessToken)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(Date.from(expiresAt))
                .signWith(secretKey)
                .compact();
    }

    public String createJwt(String principalName, String provider, String accessToken,  Date expiresAt) {

        return Jwts.builder()
                .claim("principalName", principalName)
                .claim("provider", provider)
                .claim("accessToken", accessToken)
                .claim("accessTokenReissueAt", expiresAt)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(expiresAt)
                .signWith(secretKey)
                .compact();
    }

    public boolean validateWithJwtIssuedAt(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Date issuedAt = claims.getIssuedAt();

        if (issuedAt == null) {
            return false; // issuedAt이 없으면 판별 불가 (또는 false 처리)
        }

        long tenMinutesInMillis = 10 * 60 * 1000;
        long now = System.currentTimeMillis();

        return now - issuedAt.getTime() >= tenMinutesInMillis;
    }

    public Boolean validateWithClaims(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                return false;
            }

            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // 기본 검증
            if (claims.getExpiration().before(new Date())) {
                return false;
            }

            // 필수 클레임 존재 여부 확인
            String principalName = claims.get("principalName", String.class);
            String provider = claims.get("provider", String.class);

            return principalName != null && !principalName.trim().isEmpty() &&
                    provider != null && !provider.trim().isEmpty();

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }


}
