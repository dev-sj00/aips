package com.portfolio.aips.project.users.service;

import com.portfolio.aips.project.users.domain.UsersEntity;
import com.portfolio.aips.project.users.domain.SocialLoginInfo;
import com.portfolio.aips.project.users.dto.request.SaveUserTokenRequest;
import com.portfolio.aips.project.users.enums.UserRole;
import com.portfolio.aips.project.users.repo.UsersRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UsersRepository usersRepository;

    private final EntityManager entityManager;


    private UsersEntity getUserEntity(SaveUserTokenRequest request) {

        return UsersEntity.
                builder()
                .role(UserRole.ROLE_USER)
                .nickname("익명")
                .scInfo(SocialLoginInfo.builder()
                        .principalName(request.principalName())
                        .provider(request.provider())
                        .refreshToken(request.refreshToken())
                        .accessToken(request.accessToken())
                        .expiresAt(request.expiresAt())
                        .build())
                .build();
    }

    @Override
    @Transactional
    public void saveOrUpdateTokenProc(SaveUserTokenRequest request) {
        UsersEntity usersEntity = usersRepository
                .findByScInfo_PrincipalNameAndScInfo_Provider(request.principalName(), request.provider())
                .orElseGet(() -> getUserEntity(request));

        SocialLoginInfo scInfo = usersEntity.getScInfo();
        log.info("이전 액세스 토큰: {}", scInfo.getAccessToken());
        log.info(scInfo.getAccessToken());

        if (usersEntity.getPk() == null) { //새유저
            usersRepository.save(usersEntity);
        }else{
            usersEntity.getScInfo().setRefreshToken(request.refreshToken());
            usersEntity.getScInfo().setAccessToken(request.accessToken());
            usersEntity.getScInfo().setExpiresAt(request.expiresAt());

            entityManager.flush();
            entityManager.refresh(usersEntity);
        }

    }
}
