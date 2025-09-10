package com.portfolio.aips.project.token.service;

import com.portfolio.aips.project.token.validator.dto.TokenValidationResultDTO;

public interface TokenService {

    TokenValidationResultDTO validateRefreshToken(String principalName, String provider);

    TokenValidationResultDTO validateAccessToken(String token);

}
