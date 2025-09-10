package com.portfolio.aips.project.token.validator.dto;

import com.portfolio.aips.project.token.validator.enums.TokenStatus;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Getter
@Slf4j
public class TokenValidationResultDTO {
    private TokenStatus status;
    private String message;
    private String newAccessToken; // 갱신된 경우

    public TokenValidationResultDTO(TokenStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public TokenValidationResultDTO(TokenStatus status, String message, String newAccessToken) {
        this.status = status;
        this.message = message;
        this.newAccessToken = newAccessToken;
    }

    public boolean isValid() {
        return status == TokenStatus.VALID;
    }


    public boolean isValidAccessToken(String prevAccessToken) {
        log.info("isEqual: {} , {}", newAccessToken, prevAccessToken);
        return Objects.equals(newAccessToken, prevAccessToken);
    }


    public boolean shouldUpdateAccessToken() {
        return status ==  TokenStatus.UPDATE;
    }




}
