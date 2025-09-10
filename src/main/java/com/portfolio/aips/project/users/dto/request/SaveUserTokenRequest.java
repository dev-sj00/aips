package com.portfolio.aips.project.users.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SaveUserTokenRequest(
        @NotNull(message = "사용자 이메일은 필수입니다")
        @NotBlank(message = "사용자 이메일을 입력해주세요")
        @Email(message = "올바른 이메일 형식을 입력해주세요")
        String principalName,

        @NotNull(message = "소셜 로그인 제공자는 필수입니다")
        @NotBlank(message = "제공자 정보를 입력해주세요")
        String provider,

        @NotNull(message = "리프레시 토큰은 필수입니다")
        @NotBlank(message = "리프레시 토큰을 입력해주세요")
        String refreshToken,


        @NotNull(message = "액세스 토큰은 필수입니다")
        @NotBlank(message = "액세스 토큰을 입력해주세요")
        String accessToken,


        @NotNull(message = "JWT 파기 날짜는 필수입니다")
        @NotBlank(message = "JWT 파기 날짜를 입력해주세요")
        java.time.Instant expiresAt
) {

}