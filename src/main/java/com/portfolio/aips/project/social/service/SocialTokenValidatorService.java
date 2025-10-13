package com.portfolio.aips.project.social.service;

import com.portfolio.aips.project.social.provider.dto.SocialTokenValidationResultDTO;

public interface SocialTokenValidatorService {

    SocialTokenValidationResultDTO validateToken(String token);


}
