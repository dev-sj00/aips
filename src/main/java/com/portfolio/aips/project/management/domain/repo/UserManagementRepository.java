package com.portfolio.aips.project.management.domain.repo;

import com.portfolio.aips.project.users.enums.UserRole;
import com.portfolio.aips.project.management.domain.vo.FindAllUsersResultVO;
import org.springframework.data.domain.Page;

public interface UserManagementRepository {
    Page<FindAllUsersResultVO> findAllUsersWithOffsetAndLimit(int page, int size);
    void updateUserRoleByUserPk(Long userPk, UserRole role);
    void createReportInProgressByAdminUserPkAndTargetUserPk(Long adminUserPk, Long targetUserPk);
}
