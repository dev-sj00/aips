package com.portfolio.aips.project.management.app.service.user_management;

import com.portfolio.aips.project.management.app.service.user_management.command.CreateReportInProgressProcCommand;
import com.portfolio.aips.project.management.app.service.user_management.command.FindAllUsersProcCommand;
import com.portfolio.aips.project.management.app.service.user_management.command.UpdateUserRoleProcCommand;
import com.portfolio.aips.project.management.domain.repo.UserManagementRepository;
import com.portfolio.aips.project.management.domain.vo.FindAllUsersResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final UserManagementRepository userManagementRepository;

    public Page<FindAllUsersResultVO> findAllUsersProc(FindAllUsersProcCommand command)
    {

        int page = command.page();
        int size = command.size();

        return userManagementRepository.findAllUsersWithOffsetAndLimit(page, size);
    }

    public void updateUserRoleProc(UpdateUserRoleProcCommand command)
    {
        userManagementRepository.updateUserRoleByUserPk(command.userPk(), command.role());

    }
    public void createReportInProgressProc(CreateReportInProgressProcCommand command)
    {
        Long adminUserPk = command.adminUserPk();
        Long targetUserPk = command.targetUserPk();

        userManagementRepository.createReportInProgressByAdminUserPkAndTargetUserPk(adminUserPk, targetUserPk);
    }

}
