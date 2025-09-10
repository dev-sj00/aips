package com.portfolio.aips.project.users.service;

import com.portfolio.aips.project.users.dto.request.SaveUserTokenRequest;

public interface UserService {

    void saveOrUpdateTokenProc(SaveUserTokenRequest saveUserTokenRequest);
}
