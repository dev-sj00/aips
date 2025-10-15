package com.portfolio.aips.project.utils.dto;

public record JwtRefreshTokenCreateDTO(String principalName, String provider, String socialToken) {
}
