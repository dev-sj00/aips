package com.portfolio.aips.project.social.service;

import com.portfolio.aips.project.social.provider.dto.SocialTokenValidationResultDTO;

public interface SocialTokenService {

    SocialTokenValidationResultDTO validateAndRefreshAccessToken(String principalName, String provider);

    SocialTokenValidationResultDTO validateAccessToken(String token, String principalName, String provider);

}
