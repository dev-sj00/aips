package com.portfolio.aips.project.utils.dto;

import lombok.Builder;

import java.util.Date;


//3번쨰 인자는 SuccessHandler에서만사용
@Builder
public record CreateAcTokenDTO(Long userPk,  Date issuedAt) {
}

