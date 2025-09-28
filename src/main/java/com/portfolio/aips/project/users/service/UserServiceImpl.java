package com.portfolio.aips.project.users.service;

import com.portfolio.aips.project.users.domain.UsersEntity;
import com.portfolio.aips.project.users.domain.RefreshTokenEntity;
import com.portfolio.aips.project.social.dto.SaveSocialRefreshTokenInfoRequestDTO;
import com.portfolio.aips.project.social.dto.SaveSocialUserInfoRequestDTO;
import com.portfolio.aips.project.users.enums.UserRole;
import com.portfolio.aips.project.users.repo.RefreshTokenRepository;
import com.portfolio.aips.project.users.repo.UsersRepository;

import jakarta.persistence.EntityManager;
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



    private UsersEntity getNewUserEntity(SaveSocialUserInfoRequestDTO userReq, SaveSocialRefreshTokenInfoRequestDTO refreshTokenReq) {

        return UsersEntity.
                builder()
                .role(UserRole.ROLE_USER)
                .nickname("익명")
                .principalName(userReq.principalName())
                .provider(userReq.provider())
                .socialRefreshToken(refreshTokenReq.refreshToken())
                .build();
    }

    private RefreshTokenEntity getNewRefreshTokenEntity(SaveSocialRefreshTokenInfoRequestDTO refreshTokenReq) {
        return RefreshTokenEntity.builder()
                .deviceId(refreshTokenReq.deviceId())
                .userAgent(refreshTokenReq.userAgent())
                .refreshToken(refreshTokenReq.refreshToken())
                .expiresAt(refreshTokenReq.expiresAt())
                .build();
    }


     

    @Override
    @Transactional
    public void saveProc(SaveSocialUserInfoRequestDTO userReq, SaveSocialRefreshTokenInfoRequestDTO refreshTokenReq) {
        UsersEntity usersEntity = usersRepository
                .findByPrincipalNameAndProvider(userReq.principalName(), userReq.provider())
                .orElseGet(() -> getNewUserEntity(userReq, refreshTokenReq));



        if (usersEntity.getPk() == null) { //새유저
            usersEntity.addRefreshToken(getNewRefreshTokenEntity(refreshTokenReq));
        }else{
            Optional<RefreshTokenEntity> refreshTokenEntityOpt = refreshTokenRepository.findByUserAgent(refreshTokenReq.userAgent());

            if(refreshTokenEntityOpt.isPresent())
            {
                RefreshTokenEntity refreshTokenEntity = refreshTokenEntityOpt.get();
                refreshTokenEntity.setRefreshToken(refreshTokenReq.refreshToken());
                refreshTokenEntity.setExpiresAt(refreshTokenReq.expiresAt());
            }else{ //새로운 환경에서 접근이므로 refreshToken 생성
                usersEntity.addRefreshToken(getNewRefreshTokenEntity(refreshTokenReq));

            }


        }
        usersRepository.save(usersEntity);

    }
}
