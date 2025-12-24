package com.portfolio.aips.project.users.service.user;

import com.portfolio.aips.project.users.entity.QUsersEntity;
import com.portfolio.aips.project.users.entity.UsersEntity;
import com.portfolio.aips.project.users.entity.RefreshTokenEntity;
import com.portfolio.aips.project.social.dto.SaveSocialRefreshTokenInfoRequestDTO;
import com.portfolio.aips.project.social.dto.SaveSocialUserInfoRequestDTO;
import com.portfolio.aips.project.users.enums.UserRole;
import com.portfolio.aips.project.users.repo.RefreshTokenRepository;
import com.portfolio.aips.project.users.repo.UsersRepository;

import com.portfolio.aips.project.utils.JwtUtils;
import com.portfolio.aips.project.utils.dto.CreateAcTokenDTO;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UsersRepository usersRepository;
    private final JwtUtils jwtUtils;
    private final JPAQueryFactory queryFactory;


    private UsersEntity getNewUserEntity(SaveSocialUserInfoRequestDTO userReq) {

        return UsersEntity.
                builder()
                .role(UserRole.ROLE_USER)
                .nickname("익명")
                .principalName(userReq.principalName())
                .provider(userReq.provider())
                .build();
    }




    @Override
    @Transactional
    public UsersEntity saveProc(SaveSocialUserInfoRequestDTO userReq) {
        UsersEntity usersEntity = usersRepository
                .findByPrincipalNameAndProviderAndUserAgent(
                        userReq.principalName(),
                        userReq.provider(),
                        userReq.userAgent()
                )
                .orElseGet(() -> getNewUserEntity(userReq));







        usersRepository.save(usersEntity);

        return usersEntity;
/*
        SaveProcResultDTO resultDTO = new SaveProcResultDTO();
        ReusedRefreshTokenResponseDTO resultTokenResponseDTO = new ReusedRefreshTokenResponseDTO();






        if (usersEntity.getPk() != null) { //새유저 && 새로운 환경
            Optional<RefreshTokenEntity> refreshTokenEntityOpt = usersEntity.getRefreshTokenEntity().stream().findFirst();

            if(refreshTokenEntityOpt.isPresent())
            {
                log.info("같은 브라우저 & 앱 환경에서 접근");
                RefreshTokenEntity refreshTokenEntity = refreshTokenEntityOpt.get();
                updateRefreshToken(refreshTokenEntity, refreshTokenReq.expiresAt(), usersEntity.getPk(), usersEntity.getNickname());


                String prevDeviceId = refreshTokenEntity.getDeviceId();
                resultDTO.setUserEnvType(UserEnvironmentType.SAME_ENVIRONMENT); //새로운 환경이라는걸 반환값으로 설정
                resultTokenResponseDTO.setDeviceId(prevDeviceId); //기존 db 조회한 deviceId 반환 값설정
                resultTokenResponseDTO.setRefreshToken(refreshTokenEntity.getRefreshToken());
                return resultDTO;
            }

        }


        log.info("새로운 유저 또는 새로운 접근 환경");
        resultDTO.setUserEnvType(UserEnvironmentType.NEW_ENVIRONMENT);
        refreshTokenEntity = getNewRefreshTokenEntity(refreshTokenReq);
        refreshTokenEntity.setRefreshToken(generateAccessToken(usersEntity.getPk(), usersEntity.getNickname(), Date.from(Instant.now())));
        usersEntity.addRefreshToken(refreshTokenEntity);


        resultDTO.setReusedRefreshTokenResponseDTO(resultTokenResponseDTO);

        return resultDTO;*/

    }

    @Override
    @Cacheable(
            cacheNames = "userNicknameCache",
            key = "#userPk",
            unless = "#result == null"
    )
    public String findUserNickName(Long userPk) {
        QUsersEntity qUsers = QUsersEntity.usersEntity;

        return
                queryFactory
                        .select(qUsers.nickname)
                        .from(qUsers)
                        .where(qUsers.pk.eq(userPk))
                        .fetchOne();

    }


    private void updateRefreshToken(RefreshTokenEntity refreshTokenEntity, Instant expiresAt, Long pk, String nickname) {
        refreshTokenEntity.setExpiresAt(expiresAt); // db 유통기한 늘림
        refreshTokenEntity.setRefreshToken(generateAccessToken(pk, nickname, Date.from(Instant.now())));
    }

    private String generateAccessToken(Long userPk, String userName, Date issuedAt)
    {
        Instant now = Instant.now();

        Instant expiry = now.plus(1, ChronoUnit.MINUTES); // 로그인 처음할경우 access token

        return  jwtUtils.createJwt(CreateAcTokenDTO.builder()
                .userPk(userPk)
                .issuedAt(issuedAt)
                .build(), Date.from(expiry));
    }
}
