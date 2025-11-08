package com.portfolio.aips.project.users.dto;

import com.portfolio.aips.project.users.enums.UserEnvironmentType;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RefreshSaveProcResultDTO {
    private UserEnvironmentType userEnvType;
    private ReusedRefreshTokenResponseDTO reusedRefreshTokenResponseDTO;
}
