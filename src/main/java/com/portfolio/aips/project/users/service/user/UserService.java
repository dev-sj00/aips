package com.portfolio.aips.project.users.service.user;


import com.portfolio.aips.project.social.dto.SaveSocialUserInfoRequestDTO;
import com.portfolio.aips.project.users.entity.UsersEntity;


public interface UserService {

    UsersEntity saveProc(SaveSocialUserInfoRequestDTO saveUserTokenRequest);

    String findUserNickName(Long userPk);
}
