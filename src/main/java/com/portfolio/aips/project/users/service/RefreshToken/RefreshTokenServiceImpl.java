package com.portfolio.aips.project.users.service.RefreshToken;

import com.portfolio.aips.project.social.dto.SaveSocialRefreshTokenInfoRequestDTO;
import com.portfolio.aips.project.users.dto.RefreshSaveProcResultDTO;
import com.portfolio.aips.project.users.dto.ReusedRefreshTokenResponseDTO;
import com.portfolio.aips.project.users.entity.RefreshTokenEntity;
import com.portfolio.aips.project.users.entity.UsersEntity;
import com.portfolio.aips.project.users.enums.UserEnvironmentType;
import com.portfolio.aips.project.utils.JwtUtils;
import com.portfolio.aips.project.utils.dto.CreateAcTokenDTO;
import com.portfolio.aips.project.utils.dto.CreateRfTokenDTO;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService{

    private final JwtUtils jwtUtils;
    private final EntityManager entityManager;

    private RefreshTokenEntity getNewRefreshTokenEntity(SaveSocialRefreshTokenInfoRequestDTO refreshTokenReq) {
        log.info("refreshTokenReq={}", RefreshTokenEntity.builder()
                .deviceId(refreshTokenReq.deviceId())
                .userAgent(refreshTokenReq.userAgent())
                .expiresAt(refreshTokenReq.expiresAt())
                .build());

        return RefreshTokenEntity.builder()
                .deviceId(refreshTokenReq.deviceId())
                .userAgent(refreshTokenReq.userAgent())
                .expiresAt(refreshTokenReq.expiresAt())
                .build();
    }


    @Override
    @Transactional
    public RefreshSaveProcResultDTO saveProc(SaveSocialRefreshTokenInfoRequestDTO requestDTO, UsersEntity usersEntity) {

        RefreshSaveProcResultDTO resultDTO = new RefreshSaveProcResultDTO();

        usersEntity = entityManager.merge(usersEntity); //준영속 -> 영속



        Optional<RefreshTokenEntity> refreshTokenEntityOpt = usersEntity.getRefreshTokenEntity().stream().findFirst();

        if(refreshTokenEntityOpt.isPresent())
        {
                log.info("같은 브라우저 & 앱 환경에서 접근");
                RefreshTokenEntity refreshTokenEntity = refreshTokenEntityOpt.get();
                updateRefreshToken(refreshTokenEntity, requestDTO.expiresAt(), usersEntity.getPk());


                String prevDeviceId = refreshTokenEntity.getDeviceId();
                resultDTO.setUserEnvType(UserEnvironmentType.SAME_ENVIRONMENT); //새로운 환경이라는걸 반환값으로 설정

                resultDTO.setReusedRefreshTokenResponseDTO(new ReusedRefreshTokenResponseDTO(prevDeviceId, refreshTokenEntity.getRefreshToken(), usersEntity.getPk()));
                return resultDTO;
        }

        log.info("새로운 유저 또는 새로운 접근 환경");
        String newRefreshToken = insertRefreshToken(usersEntity, requestDTO);
        String newDeviceId = requestDTO.deviceId();
        resultDTO.setUserEnvType(UserEnvironmentType.NEW_ENVIRONMENT);

        resultDTO.setReusedRefreshTokenResponseDTO(new ReusedRefreshTokenResponseDTO(newDeviceId, newRefreshToken,  usersEntity.getPk()));

        return resultDTO;

    }

    private String insertRefreshToken(UsersEntity usersEntity, SaveSocialRefreshTokenInfoRequestDTO requestDTO) {
        Long userPk = usersEntity.getPk();
        String provider =  requestDTO.provider();
        String socialRefreshToken = requestDTO.socialRefreshToken();

        RefreshTokenEntity refreshTokenEntity = getNewRefreshTokenEntity(requestDTO);
        String refreshToken = generateRefreshToken(
                userPk,
                provider,
                socialRefreshToken,
                Date.from(Instant.now())
        );

        refreshTokenEntity.setRefreshToken(refreshToken);
        usersEntity.addRefreshToken(refreshTokenEntity);

        return refreshToken;
    }


    //expires, refresh token update
    private void updateRefreshToken(RefreshTokenEntity refreshTokenEntity, Instant expiresAt, Long pk) {

        refreshTokenEntity.setExpiresAt(expiresAt); // db 유통기한 늘림
        String provider = refreshTokenEntity.getUsersEntity().getProvider();
        String socialRefreshToken = refreshTokenEntity.getRefreshToken();

        refreshTokenEntity.setRefreshToken(generateRefreshToken(pk, provider, socialRefreshToken, Date.from(Instant.now()))); // 유통기한 늘린
    }


    private String generateRefreshToken(Long userPk, String provider, String socialToken, Date issuedAt)
    {

        return  jwtUtils.createJwt(CreateRfTokenDTO.builder()
                .userPk(userPk)
                .provider(provider)
                .socialToken(socialToken)
                .issuedAt(issuedAt)
                .build());
    }

}
