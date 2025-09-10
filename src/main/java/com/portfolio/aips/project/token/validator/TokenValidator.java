package com.portfolio.aips.project.token.validator;

import com.portfolio.aips.project.token.validator.dto.TokenValidationResultDTO;





public interface TokenValidator {
    TokenValidationResultDTO validateAndGetAccessToken(String refreshToken);
}
