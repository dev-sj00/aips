package com.portfolio.aips.project.users.service.user;

import com.portfolio.aips.project.interaction.common.enums.BoardType;
import com.portfolio.aips.project.interaction.report.domain.entity.ReportEntity;
import com.portfolio.aips.project.interaction.report.domain.model.ReportStatus;
import com.portfolio.aips.project.interaction.report.domain.model.ReportType;
import com.portfolio.aips.project.interaction.report.infra.ReportRepository;
import com.portfolio.aips.project.users.entity.UsersEntity;
import com.portfolio.aips.project.users.enums.UserRole;
import com.portfolio.aips.project.users.repo.UsersRepository;
import com.portfolio.aips.project.users.service.user.result.FindAllUsersResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService{

    private final ReportRepository reportRepository;
    private final UsersRepository usersRepository;


    @Override
    public Page<FindAllUsersResult> findAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<UsersEntity> entities = usersRepository.findAll(pageable);

        return entities.map(user -> new FindAllUsersResult(
                user.getPk(),
                user.getNickname(),
                user.getPrincipalName(),
                user.getProvider(),
                user.getRole(),
                user.getCreatedDateTime()
        ));
    }

    @Override
    @Transactional
    public void updateUserRole(Long userPk, UserRole role) {
        Optional<UsersEntity> userEntity = usersRepository.findById(userPk);

        userEntity.ifPresent(user -> user.setRole(role));
    }

    @Override
    public void createReportInProgress(Long adminUserPk) {
        reportRepository.save(ReportEntity.builder()
                        .reportType(ReportType.ETC)
                        .reporterUserPk(adminUserPk)
                        .reportUrl("운영자 신고")
                        .reportContent("운영자가 신고한 유저입니다.")
                        .boardType(BoardType.Archive)
                        .reportStatus(ReportStatus.IN_PROGRESS)
                        .build());
    }
}
