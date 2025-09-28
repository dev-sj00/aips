package com.portfolio.aips.project.social.provider;

import com.portfolio.aips.project.social.provider.dto.SocialTokenValidationResultDTO;





public interface SocialTokenProvider {
    SocialTokenValidationResultDTO refreshAccessToken(String refreshToken);

}
