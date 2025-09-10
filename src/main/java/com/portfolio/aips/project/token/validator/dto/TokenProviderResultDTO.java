package com.portfolio.aips.project.token.validator.dto;

import com.portfolio.aips.project.token.validator.enums.TokenStatus;
import lombok.Data;

@Data
public class TokenProviderResultDTO {
    private String accessToken;
    private String accessTokenExpireTime;
    private TokenStatus tokenStatus;

}
