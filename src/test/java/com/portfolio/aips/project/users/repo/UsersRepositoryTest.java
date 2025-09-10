package com.portfolio.aips.project.users.repo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UsersRepositoryTest {

    @Autowired
    private UsersRepository usersRepository;


    @Test
    void setUp() {
      /*  SocialLoginInfo testSocialInfo = new SocialLoginInfo(
                "example@naver.com",
                "google",
                "test_refresh_token",
                Instant.now()
        );

        UsersEntity testUser = UsersEntity.builder()

                .nickname("테스트유저")
                .role(UserRole.ROLE_USER)
                .socialLoginInfo(testSocialInfo)
                .build();

        usersRepository.save(testUser);

        System.out.println(usersRepository.findBySocialLoginInfo_ProviderAndSocialLoginInfo_Provider(testUser.getScInfo().getProvider(), testUser.getScInfo().getProvider()));*/
    }

    @AfterEach
    void tearDown() {
        usersRepository.deleteAll();
    }
}