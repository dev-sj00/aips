package com.portfolio.aips.project.utils.dto;

import lombok.Builder;

import java.util.Date;



@Builder
public record CreateRfTokenDTO(Long userPk, String provider, String socialToken, Date issuedAt) {
}
