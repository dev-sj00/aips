package com.portfolio.aips.project.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
public class TokenPairDTO {
    private final String accessToken;
    private final String refreshToken;



}
