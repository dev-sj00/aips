package com.portfolio.aips.project.utils;


import com.portfolio.aips.project.social.provider.enums.TokenStatus;
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


    public final int REFRESH_TOKEN_EXPIRATION;
    public final int ACCESS_TOKEN_EXPIRATION;


    public JwtUtils(@Value("${spring.jwt.secret}") String secret, @Value("${spring.jwt.refresh-token-expiration}") int REFRESH_TOKEN_EXPIRATION, @Value("${spring.jwt.access-token-expiration}") int ACCESS_TOKEN_EXPIRATION) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.REFRESH_TOKEN_EXPIRATION = REFRESH_TOKEN_EXPIRATION;
        this.ACCESS_TOKEN_EXPIRATION = ACCESS_TOKEN_EXPIRATION;
    }

    public <T> T getJWTExpiredTime(String tokenType, Class<T> requiredType)
    {
        int expirationSeconds = getExpirationSeconds(tokenType);

        if(requiredType == Instant.class) {
            return requiredType.cast(Instant.now().plusSeconds(expirationSeconds));
        }

        if(requiredType == int.class) {
            return requiredType.cast(REFRESH_TOKEN_EXPIRATION);
        }

        throw new JwtException("Invalid token type or required type: " + requiredType.getSimpleName());
    }

    private int getExpirationSeconds(String tokenType) {
        return switch (tokenType) {
            case "refresh_token" -> REFRESH_TOKEN_EXPIRATION;
            case "access_token" -> ACCESS_TOKEN_EXPIRATION;
            default -> throw new JwtException("Invalid token type: " + tokenType);
        };
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



    public String createJwt(String principalName, String provider, Instant expiresAt) {

        return Jwts.builder()
                .claim("principalName", principalName)
                .claim("provider", provider)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(Date.from(expiresAt))
                .signWith(secretKey)
                .compact();
    }

    public String createJwt(String accessToken,  Date expiresAt) {

        return Jwts.builder()
                .claim("accessToken", accessToken)
                .claim("accessTokenReissueAt", expiresAt)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(expiresAt)
                .signWith(secretKey)
                .compact();
    }



    public TokenStatus validateWithClaims(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                return TokenStatus.NOT_EXISTS;
            }

            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // 기본 검증
            if (claims.getExpiration().before(new Date())) {
                return TokenStatus.UPDATE;
            }

            // 필수 클레임 존재 여부 확인
            String principalName = claims.get("principalName", String.class);
            String provider = claims.get("provider", String.class);

            return (principalName != null && !principalName.trim().isEmpty() &&
                    provider != null && !provider.trim().isEmpty())
                    ? TokenStatus.VALID
                    : TokenStatus.NOT_EXISTS;

        } catch (JwtException | IllegalArgumentException e) {

            return TokenStatus.ERROR;
        }
    }


}
