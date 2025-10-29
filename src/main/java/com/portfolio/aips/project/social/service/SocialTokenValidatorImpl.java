package com.portfolio.aips.project.social.service;


import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;
import com.portfolio.aips.project.social.provider.SocialTokenProvider;
import com.portfolio.aips.project.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocialTokenValidatorImpl implements SocialTokenValidator {

    private final ApplicationContext applicationContext;
    private final JwtUtils jwtUtils;

    @Override
    public void validateToken(String token) {

        String provider = jwtUtils.getProvider(token);
        String socialToken = jwtUtils.getSocialToken(token);
        String beanName = provider + "TokenValidator";
        SocialTokenProvider validator = applicationContext.getBean(beanName, SocialTokenProvider.class);

        if(!validator.refreshAccessToken(socialToken).isValid())
        {
            log.error("Social token validation failed");
            throw new CustomException(ErrorCode.INVALID_SOCIAL_REFRESH_TOKEN);
        }


    }



}
