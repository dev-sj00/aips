package com.portfolio.aips.project.url_service.protect_url.service.protect_url;

import com.portfolio.aips.project.url_service.protect_url.dto.request.PasswordCreateRequest;
import com.portfolio.aips.project.users.dto.CustomUserDetails;
import com.portfolio.aips.project.users.repo.UsersRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProtectURLServiceTest {

    @Autowired
    private ProtectURLService protectURLService;

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private EntityManager em;



    @Test
    @Transactional
    void createProtectUrl_success() {
        // given - 실제 User 객체 저장


        // 실제 Request DTO 생성
        PasswordCreateRequest request = new PasswordCreateRequest();
        request.setPassword("abcd1234");
        request.setConfirmPassword("abcd1234");
        request.setUrlLink("https://claude.ai/share/9b7cebf3-ca49-4d91-a7ff-937f71a73aa9");
        request.setTitle("테스트 제목");
        request.setDescription("테스트 설명입니다.");




        //Long userId, String provider, String password, String authorities
        CustomUserDetails customUserDetail = CustomUserDetails.build(7L, "google", "3232", "ROLE");

        // when
        String slugUrl = protectURLService.createProtectUrl(request, customUserDetail);
        em.flush();

        // then
        assertNotNull(slugUrl);
        assertFalse(slugUrl.isBlank());

        // DB에 저장됐는지 확인

    }
}