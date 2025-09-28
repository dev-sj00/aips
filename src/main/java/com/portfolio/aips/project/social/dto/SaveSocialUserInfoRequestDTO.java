package com.portfolio.aips.project.social.dto;

public record SaveSocialUserInfoRequestDTO(
        String principalName,
        String provider,
        String socialRefreshToken

) {

}