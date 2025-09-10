package com.portfolio.aips.project.token.service;


import com.portfolio.aips.project.users.domain.UsersEntity;
import com.portfolio.aips.project.users.domain.SocialLoginInfo;
import com.portfolio.aips.project.users.repo.UsersRepository;
import com.portfolio.aips.project.token.validator.TokenValidator;
import com.portfolio.aips.project.token.validator.dto.TokenValidationResultDTO;
import com.portfolio.aips.project.token.validator.enums.TokenStatus;
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
public class TokenServiceImpl implements TokenService {

    private final UsersRepository usersRepository;
    private final ApplicationContext applicationContext;
    private final EntityManager entityManager;
    private final JwtUtils jwtUtils;

    @Override
    public TokenValidationResultDTO validateRefreshToken(String principalName, String provider) {
        Optional<UsersEntity> usersEntityOpt = usersRepository.findByScInfo_PrincipalNameAndScInfo_Provider(principalName, provider);


        if(usersEntityOpt.isEmpty()) {
            return new TokenValidationResultDTO(TokenStatus.NOT_EXISTS, "유저 entity 정보가 없습니다.");
        }

        SocialLoginInfo tokenEntity = usersEntityOpt.get().getScInfo();


        if ( usersEntityOpt.get().getScInfo().getRefreshToken().isEmpty()) {
                return new TokenValidationResultDTO(TokenStatus.NOT_EXISTS, "RefreshToken이 저장되어 있지 않습니다.");
        }


        // DB 상에서 만료된 경우
        if (tokenEntity.isExpired()) {
            return new TokenValidationResultDTO(TokenStatus.EXPIRED, "저장된 RefreshToken 만료되었습니다.");
        }

        // 실제 OAuth2 제공자에게 확인
        String beanName = provider + "TokenValidator";
        TokenValidator validator = applicationContext.getBean(beanName, TokenValidator.class);
        return validator.validateAndGetAccessToken(tokenEntity.getRefreshToken());

    }

    @Override
    @Transactional
    public TokenValidationResultDTO validateAccessToken(String token) {

        String principalName = jwtUtils.getPrincipalName(token);
        String provider = jwtUtils.getProvider(token);
        String jwtAccessToken = jwtUtils.getAccessToken(token);



        //1. validateRefreshToken() 실행 : return access token
        TokenValidationResultDTO dto = validateRefreshToken(principalName, provider); //한번 날림
        if (!dto.isValid())  //refresh token이 만료 되거나 문제가 생김
        {
            return dto;
        }



        Optional<UsersEntity> userEntityOpt = usersRepository.findByScInfo_PrincipalNameAndScInfo_Provider(principalName, provider);


        if (userEntityOpt.isEmpty()) {
            return new TokenValidationResultDTO(TokenStatus.NOT_EXISTS, "소셜 로그인 정보가 없습니다.");
        } else {
            UsersEntity userEntity = userEntityOpt.get();
            SocialLoginInfo scInfo = userEntity.getScInfo();

            //DB <-> jwt access token 값 비교 다를 시 status 다름으로 반환 -> 로그아웃
            log.info("jwtAccess {}, dbAccess {}", jwtAccessToken, scInfo.getAccessToken());
            if (!scInfo.isValidAccessToken(jwtAccessToken)) {
                return new TokenValidationResultDTO(TokenStatus.Failed, "다른 환경에서 로그인 하였습니다.");
            }


            //구현 중
            else if (!dto.isValidAccessToken(jwtAccessToken)) {
                String newAccessToken = dto.getNewAccessToken();
                entityManager.flush();
                entityManager.refresh(userEntity);


                return new TokenValidationResultDTO(TokenStatus.UPDATE, "액세스 토큰 만료, 쿠키를 업데이트 해야합니다.", newAccessToken);
            } else {

                return new TokenValidationResultDTO(TokenStatus.VALID, "토큰 검증 완료");
            }
        }


    }


}
