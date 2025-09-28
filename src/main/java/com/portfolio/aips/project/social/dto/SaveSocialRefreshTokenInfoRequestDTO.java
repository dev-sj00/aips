package com.portfolio.aips.project.social.dto;

public record SaveSocialRefreshTokenInfoRequestDTO(
        String deviceId,
        String refreshToken,
        String userAgent,
        java.time.Instant expiresAt
) {
}
