package com.portfolio.aips.project.users.service;

import com.portfolio.aips.project.social.dto.SaveSocialRefreshTokenInfoRequestDTO;
import com.portfolio.aips.project.social.dto.SaveSocialUserInfoRequestDTO;
import com.portfolio.aips.project.users.dto.SaveProcResultDTO;
import com.portfolio.aips.project.users.enums.UserEnvironmentType;

public interface UserService {

    SaveProcResultDTO saveProc(SaveSocialUserInfoRequestDTO saveUserTokenRequest, SaveSocialRefreshTokenInfoRequestDTO saveSocialRefreshTokenInfoRequestDTO);
}
