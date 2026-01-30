package com.portfolio.aips.project.users.service.user;

import com.portfolio.aips.project.users.enums.UserRole;
import com.portfolio.aips.project.users.service.user.result.FindAllUsersResult;
import org.springframework.data.domain.Page;

public interface UserManagementService {
    Page<FindAllUsersResult> findAllUsers(int page, int size);
    void updateUserRole(Long userPk, UserRole role);
    void createReportInProgress(Long adminUserPk, Long targetUserPk);
}
