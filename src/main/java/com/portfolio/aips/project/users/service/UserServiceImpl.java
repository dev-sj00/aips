package com.portfolio.aips.project.users.service;

import com.portfolio.aips.project.users.domain.UsersEntity;
import com.portfolio.aips.project.users.domain.RefreshTokenEntity;
import com.portfolio.aips.project.social.dto.SaveSocialRefreshTokenInfoRequestDTO;
import com.portfolio.aips.project.social.dto.SaveSocialUserInfoRequestDTO;
import com.portfolio.aips.project.users.dto.ReusedRefreshTokenResponseDTO;
import com.portfolio.aips.project.users.dto.SaveProcResultDTO;
import com.portfolio.aips.project.users.enums.UserEnvironmentType;
import com.portfolio.aips.project.users.enums.UserRole;
import com.portfolio.aips.project.users.repo.RefreshTokenRepository;
import com.portfolio.aips.project.users.repo.UsersRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UsersRepository usersRepository;
    private final RefreshTokenRepository refreshTokenRepository;



    private UsersEntity getNewUserEntity(SaveSocialUserInfoRequestDTO userReq) {

        return UsersEntity.
                builder()
                .role(UserRole.ROLE_USER)
                .nickname("익명")
                .principalName(userReq.principalName())
                .provider(userReq.provider())
                .build();
    }

    private RefreshTokenEntity getNewRefreshTokenEntity(SaveSocialRefreshTokenInfoRequestDTO refreshTokenReq) {
        log.info("refreshTokenReq={}", RefreshTokenEntity.builder()
                .deviceId(refreshTokenReq.deviceId())
                .userAgent(refreshTokenReq.userAgent())
                .refreshToken(refreshTokenReq.refreshToken())
                .expiresAt(refreshTokenReq.expiresAt())
                .build());

        return RefreshTokenEntity.builder()
                .deviceId(refreshTokenReq.deviceId())
                .userAgent(refreshTokenReq.userAgent())
                .refreshToken(refreshTokenReq.refreshToken())
                .expiresAt(refreshTokenReq.expiresAt())
                .build();
    }


    @Override
    @Transactional
    public SaveProcResultDTO saveProc(SaveSocialUserInfoRequestDTO userReq, SaveSocialRefreshTokenInfoRequestDTO refreshTokenReq) {

        UsersEntity usersEntity = usersRepository
                .findByPrincipalNameAndProvider(userReq.principalName(), userReq.provider())
                .orElseGet(() -> getNewUserEntity(userReq));


        log.info("refreshTokenReq: {}", refreshTokenReq);


        SaveProcResultDTO resultDTO = new SaveProcResultDTO();

        if (usersEntity.getPk() == null) { //새유저 && 새로운 환경

            log.info("새로운 유저 & 새로운 접근 환경");
            usersEntity.addRefreshToken(getNewRefreshTokenEntity(refreshTokenReq));
            resultDTO.setUserEnvType(UserEnvironmentType.NEW_ENVIRONMENT);
        }else{
            String userAgent = refreshTokenReq.userAgent();
            Optional<RefreshTokenEntity> refreshTokenEntityOpt = refreshTokenRepository.findOneByUsersEntityAndUserAgent(usersEntity, userAgent);


            if(refreshTokenEntityOpt.isPresent())
            {
                log.info("같은 브라우저 & 앱 환경에서 접근");
                RefreshTokenEntity refreshTokenEntity = refreshTokenEntityOpt.get();
                String prevDeviceId = refreshTokenEntity.getDeviceId();
                String prevRefreshToken = refreshTokenEntity.getRefreshToken();
                resultDTO.setUserEnvType(UserEnvironmentType.SAME_ENVIRONMENT);
                resultDTO.setReusedRefreshTokenResponseDTO(new ReusedRefreshTokenResponseDTO(prevDeviceId, prevRefreshToken));
                refreshTokenEntity.setExpiresAt(refreshTokenEntity.getExpiresAt());

            }else{ //새로운 환경에서 접근이므로 refreshToken 생성
                log.info("새로운 환경 접근");
                usersEntity.addRefreshToken(getNewRefreshTokenEntity(refreshTokenReq));
                resultDTO.setUserEnvType(UserEnvironmentType.NEW_ENVIRONMENT);
            }


        }
        usersRepository.save(usersEntity);

        return resultDTO;

    }
}
