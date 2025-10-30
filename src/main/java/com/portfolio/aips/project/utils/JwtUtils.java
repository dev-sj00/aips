package com.portfolio.aips.project.utils;


import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;
import com.portfolio.aips.project.social.provider.enums.TokenStatus;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
@Slf4j
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

        if(requiredType == Integer.class) {
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

    public String getSocialToken(String token) {

        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("socialToken", String.class);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // 토큰 만료
            throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        } catch (io.jsonwebtoken.JwtException e) {
            // 잘못된 토큰
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }


    public String getProvider(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("provider", String.class);
    }

    public Date getExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration();
    }

    public Date getIssuedAt(String token)
    {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getIssuedAt();
    }



    //refresh Token용
    public String createJwt(String principalName, String provider, String socialToken, Date issuedAt) {

        Instant expiresAt = getJWTExpiredTime("refresh_token", Instant.class);
        return Jwts.builder()
                .claim("principalName", principalName)
                .claim("provider", provider)
                .claim("socialToken", socialToken)
                .issuedAt(issuedAt)
                .expiration(Date.from(expiresAt))
                .signWith(secretKey)
                .compact();
    }
    //access Token 용
    public String createJwt(String principalName, String provider, Date issuedAt) {

        Instant expiresAt = getJWTExpiredTime("access_token", Instant.class);
        return Jwts.builder()
                .claim("principalName", principalName)
                .claim("provider", provider)
                .issuedAt(issuedAt)
                .expiration(Date.from(expiresAt))
                .signWith(secretKey)
                .compact();
    }
    //access token - success handler 용도
    public String createJwt(String principalName, String provider, Date issuedAt, Date expiresAt) {

        return Jwts.builder()
                .claim("principalName", principalName)
                .claim("provider", provider)
                .issuedAt(issuedAt)
                .expiration(expiresAt)
                .signWith(secretKey)
                .compact();
    }





    public String extractAccessTokenFromAuthorizationHeader(String authHeader) {

        if (authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            log.info("JWT AuthenticationFilter token: {}", token);
            return token;
        } else {
            return null;
        }
    }




}
