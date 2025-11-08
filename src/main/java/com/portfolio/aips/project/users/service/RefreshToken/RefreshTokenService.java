package com.portfolio.aips.project.users.service.RefreshToken;

import com.portfolio.aips.project.social.dto.SaveSocialRefreshTokenInfoRequestDTO;
import com.portfolio.aips.project.users.dto.RefreshSaveProcResultDTO;
import com.portfolio.aips.project.users.entity.UsersEntity;

public interface RefreshTokenService {
    RefreshSaveProcResultDTO saveProc(SaveSocialRefreshTokenInfoRequestDTO saveSocialRefreshTokenInfoRequestDTO, UsersEntity usersEntity);
}
