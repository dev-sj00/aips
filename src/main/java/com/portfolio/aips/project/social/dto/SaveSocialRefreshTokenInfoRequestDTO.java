package com.portfolio.aips.project.social.dto;

public record SaveSocialRefreshTokenInfoRequestDTO(
        String deviceId,
        String provider,
        String socialRefreshToken,
        String userAgent,
        java.time.Instant expiresAt
) {
}
