package com.portfolio.aips.project.users.service;

import com.portfolio.aips.project.social.dto.SaveSocialRefreshTokenInfoRequestDTO;
import com.portfolio.aips.project.social.dto.SaveSocialUserInfoRequestDTO;

public interface UserService {

    void saveProc(SaveSocialUserInfoRequestDTO saveUserTokenRequest, SaveSocialRefreshTokenInfoRequestDTO saveSocialRefreshTokenInfoRequestDTO);
}
