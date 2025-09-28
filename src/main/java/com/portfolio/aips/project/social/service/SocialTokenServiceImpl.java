package com.portfolio.aips.project.social.service;


import com.portfolio.aips.project.users.domain.UsersEntity;
import com.portfolio.aips.project.users.domain.RefreshTokenEntity;
import com.portfolio.aips.project.users.repo.UsersRepository;
import com.portfolio.aips.project.social.provider.SocialTokenProvider;
import com.portfolio.aips.project.social.provider.dto.SocialTokenValidationResultDTO;
import com.portfolio.aips.project.social.provider.enums.TokenStatus;
import com.portfolio.aips.project.utils.JwtUtils;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocialTokenServiceImpl implements SocialTokenService {

    private final UsersRepository usersRepository;
    private final ApplicationContext applicationContext;
    private final EntityManager entityManager;
    private final JwtUtils jwtUtils;

    @Override
    public SocialTokenValidationResultDTO validateAndRefreshAccessToken(String principalName, String provider) {
        Optional<UsersEntity> usersEntityOpt = usersRepository.findByPrincipalNameAndProvider(principalName, provider);


        if (usersEntityOpt.isEmpty()) {
            return new SocialTokenValidationResultDTO(TokenStatus.NOT_EXISTS, "유저 entity 정보가 없습니다.");
        }

        RefreshTokenEntity tokenEntity = usersEntityOpt.get().getRefreshTokenEntity();


        if (usersEntityOpt.get().getRefreshTokenEntity().getRefreshToken().isEmpty()) {
            return new SocialTokenValidationResultDTO(TokenStatus.NOT_EXISTS, "RefreshToken이 저장되어 있지 않습니다.");
        }


        // DB 상에서 만료된 경우
        if (tokenEntity.isExpired()) {
            return new SocialTokenValidationResultDTO(TokenStatus.EXPIRED, "자동 로그인이 만료되었습니다.");
        }

        // 실제 OAuth2 제공자에게 확인
        String beanName = provider + "TokenValidator";
        SocialTokenProvider validator = applicationContext.getBean(beanName, SocialTokenProvider.class);
        return validator.refreshAccessToken(tokenEntity.getRefreshToken());

    }

    @Override
    @Transactional
    public SocialTokenValidationResultDTO validateAccessToken(String token, String principalName, String provider) {


        String jwtAccessToken = jwtUtils.getAccessToken(token);


        SocialTokenValidationResultDTO dto = validateAndRefreshAccessToken(principalName, provider); //한번 날림
        if (!dto.isValid())  //refresh token이 만료 되거나 문제가 생김
        {
            return dto;
        }

        Optional<UsersEntity> userEntityOpt = usersRepository.findByPrincipalNameAndProvider(principalName, provider);


        if (userEntityOpt.isEmpty()) {
            return new SocialTokenValidationResultDTO(TokenStatus.NOT_EXISTS, "소셜 로그인 정보가 없습니다.");
        } else {
            UsersEntity userEntity = userEntityOpt.get();
            RefreshTokenEntity refreshTokenEntity = userEntity.getRefreshTokenEntity();



            //구현 중
            return validateSucceedOrRenewToken(token, dto, refreshTokenEntity, userEntity);



        }

    }

    private SocialTokenValidationResultDTO validateSucceedOrRenewToken(
            String token,
            SocialTokenValidationResultDTO dto,
            RefreshTokenEntity scInfo,
            UsersEntity userEntity
    ) {
        if (jwtUtils.validateWithClaims(token).equals(TokenStatus.UPDATE)) {
            String newAccessToken = dto.getNewAccessToken();
/*            scInfo.setAccessToken(newAccessToken);
            entityManager.flush();
            entityManager.refresh(userEntity);*/
            return new SocialTokenValidationResultDTO(
                    TokenStatus.UPDATE,
                    "액세스 토큰 만료, 쿠키를 업데이트 해야합니다.",
                    newAccessToken
            );
        }

        // else 처리: null 대신 안전한 기본 반환
        return new SocialTokenValidationResultDTO(
                TokenStatus.VALID,
                "토큰 검증 완료"
        );


    }
}
