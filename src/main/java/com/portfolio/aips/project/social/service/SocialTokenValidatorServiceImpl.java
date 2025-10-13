package com.portfolio.aips.project.social.service;


import com.portfolio.aips.project.social.provider.SocialTokenProvider;
import com.portfolio.aips.project.social.provider.dto.SocialTokenValidationResultDTO;
import com.portfolio.aips.project.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocialTokenValidatorServiceImpl implements SocialTokenValidatorService {

    private final ApplicationContext applicationContext;
    private final JwtUtils jwtUtils;

    @Override
    public SocialTokenValidationResultDTO validateToken(String token) {

        String provider = jwtUtils.getProvider(token);
        String socialToken = jwtUtils.getSocialToken(token);
        String beanName = provider + "TokenValidator";
        SocialTokenProvider validator = applicationContext.getBean(beanName, SocialTokenProvider.class);
        return validator.refreshAccessToken(socialToken);

    }



}
