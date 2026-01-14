package com.portfolio.aips.project.interaction.view.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.aips.project.config.security.SecurityConfig;
import com.portfolio.aips.project.config.security.filter.autoLogin.interfaces.AutoLoginService;
import com.portfolio.aips.project.config.security.handler.OAuth2SuccessHandler;
import com.portfolio.aips.project.interaction.enums.BoardType;
import com.portfolio.aips.project.interaction.view.controller.dto.request.IncreaseViewCountRequest;
import com.portfolio.aips.project.interaction.view.repo.dto.request.IncreaseViewCountDTO;
import com.portfolio.aips.project.interaction.view.service.view.ViewService;
import com.portfolio.aips.project.users.dto.CustomUserDetails;
import com.portfolio.aips.project.users.service.CustomUserDetailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class ViewRestControllerTest {
    @Autowired
    private MockMvc mockMvc;




    @MockitoBean
    AutoLoginService autoLoginService;

    @MockitoBean
    CustomUserDetailService customUserDetailService;


    @MockitoBean
    OAuth2SuccessHandler oAuth2SuccessHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 조회수_하트비트_API_테스트() throws Exception {
        // given
        IncreaseViewCountRequest request = new IncreaseViewCountRequest(
                1L,
                BoardType.Archive
        );

        CustomUserDetails userDetails =
                CustomUserDetails.build(
                        2L,
                        "google",
                        "없음",
                        "ROLE_USER"
                );

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // when & then
        mockMvc.perform(post("/api/v1/view/heartbeat")
                        .header(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(request1 -> {
                            request1.setRemoteAddr("127.0.0.1");

                            return request1;
                        })
                )
                .andExpect(status().isOk());

        // 서비스 호출 검증

    }
}