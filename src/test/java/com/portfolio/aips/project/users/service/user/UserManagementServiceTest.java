package com.portfolio.aips.project.users.service.user;

import com.portfolio.aips.project.interaction.common.enums.BoardType;
import com.portfolio.aips.project.interaction.report.domain.entity.ReportEntity;
import com.portfolio.aips.project.interaction.report.domain.model.ReportStatus;
import com.portfolio.aips.project.interaction.report.domain.model.ReportType;
import com.portfolio.aips.project.interaction.report.infra.ReportRepository;
import com.portfolio.aips.project.management.domain.repo.UserManagementRepository;
import com.portfolio.aips.project.users.entity.UsersEntity;
import com.portfolio.aips.project.users.enums.UserRole;
import com.portfolio.aips.project.users.repo.UsersRepository;
import com.portfolio.aips.project.management.domain.vo.FindAllUsersResultVO;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserManagementJpaRepositoryImplTest {

    @Autowired
    private UserManagementRepository userManagementRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ReportRepository reportRepository;

    private UsersEntity testUser;

    @Autowired
    private EntityManager entitymanager;

    @BeforeEach
    void setUp() {
        // 테스트용 유저 생성
        testUser = usersRepository.save(
                UsersEntity.builder()
                        .nickname("테스트유저")

                        .principalName("test@example.com")
                        .provider("LOCAL")
                        .role(UserRole.ROLE_USER)
                        .build()
        );
    }

    @Test
    void findAllUsers_returnsPagedUsersWithOffssetAndLimit() {
        // given 추가 유저 9명 생성 -> 총 10명
        for (int i = 0; i < 9; i++) {
            usersRepository.save(
                    UsersEntity.builder()
                            .nickname("유저" + i)
                            .principalName("user" + i + "@example.com")
                            .provider("LOCAL")
                            .role(UserRole.ROLE_USER)
                            .build()
            );
        }

        // when
        Page<FindAllUsersResultVO> page = userManagementRepository.findAllUsersWithOffsetAndLimit(0, 5);

        // then
        assertThat(page.getContent()).hasSize(5);
        assertThat(page.getTotalElements()).isEqualTo(12); //기존 db 2개 추가됨
        assertThat(page.getContent().get(2).nickname()).isEqualTo("테스트유저");
    }

    @Test
    void updateUserRole_changesUserRoleByUserPkAndRole() {
        // given
        assertThat(testUser.getRole()).isEqualTo(UserRole.ROLE_USER);

        // when
        userManagementRepository.updateUserRoleByUserPk(testUser.getPk(), UserRole.ROLE_ADMIN);

        entitymanager.flush();

        // then
        UsersEntity updated = usersRepository.findById(testUser.getPk()).orElseThrow();
        assertThat(updated.getRole()).isEqualTo(UserRole.ROLE_ADMIN);
    }

    @Test
    void createReportInProgress_savesReportWithInProgressByAdminUserPkAndTargetUserPkStatus() {
        // when
        userManagementRepository.createReportInProgressByAdminUserPkAndTargetUserPk(testUser.getPk(), 1L);

        // then
        List<ReportEntity> reports = reportRepository.findAll();
        assertThat(reports).hasSize(1);

        ReportEntity report = reports.getFirst();
        assertThat(report.getReporterUserPk()).isEqualTo(testUser.getPk());
        assertThat(report.getReportStatus()).isEqualTo(ReportStatus.IN_PROGRESS);
        assertThat(report.getReportType()).isEqualTo(ReportType.ETC);
        assertThat(report.getBoardType()).isEqualTo(BoardType.Archive);
        assertThat(report.getReportContent()).isEqualTo("운영자가 신고한 유저입니다.");
    }
}