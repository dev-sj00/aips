package com.portfolio.aips.project.interaction.report.app.service;

import com.portfolio.aips.project.interaction.report.app.admin.service.ReportManagementService;
import com.portfolio.aips.project.interaction.report.app.user.service.CreateReportService;
import com.portfolio.aips.project.interaction.report.app.user.service.command.CreateReportCommand;
import com.portfolio.aips.project.interaction.report.app.admin.service.result.FindAllReportHistoryWithPagingResult;
import com.portfolio.aips.project.interaction.report.app.admin.service.result.FindAllReportUsersWithPagingResult;
import com.portfolio.aips.project.interaction.report.domain.entity.ReportEntity;
import com.portfolio.aips.project.interaction.report.domain.model.BanType;
import com.portfolio.aips.project.interaction.report.domain.model.ReportStatus;
import com.portfolio.aips.project.interaction.report.domain.model.ReportType;
import com.portfolio.aips.project.interaction.report.infra.ReportRepository;
import com.portfolio.aips.project.interaction.sanction.domain.ActiveSanctionEntity;
import com.portfolio.aips.project.interaction.sanction.infra.ActiveSanctionJpaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class ReportManagementServiceTest {

    @Autowired
    private ReportManagementService reportManagementService;

    @Autowired
    private CreateReportService createReportService;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ActiveSanctionJpaRepository activeSanctionJpaRepository;

    @Autowired
    private EntityManager em;


    @Test
    @DisplayName("신고 대상 유저별 신고 건수를 페이징으로 조회함")
    @Transactional
    void findAllReportUsersWithPaging() {
        for(int i = 0; i < 15; i++) {
            createReportService.createReport(
                    new CreateReportCommand(
                            "23232",
                            1L,
                            2L,
                            ReportType.ABUSE,
                            "content"
                    )
            );
        }

        List<FindAllReportUsersWithPagingResult> result = reportManagementService.findAllReportUsersWithPaging(0, 10, ReportStatus.PENDING);

        assertThat(result.size()).isEqualTo(1);

        List< FindAllReportHistoryWithPagingResult> results2 = reportManagementService.findAllReportHistoryWithPaging(1, 10, 1);

        assertThat(results2.size()).isEqualTo(5);

    }

    @Test
    @DisplayName("이유와 밴 타입을 설정한다")
    @Transactional
    void updateReasonAndBanType()
    {
        createReportService.createReport(
                new CreateReportCommand(
                        "23232",
                        1L,
                        2L,
                        ReportType.ABUSE,
                        "content"
                )
        );

        List< FindAllReportHistoryWithPagingResult> results1 = reportManagementService.findAllReportHistoryWithPaging(1, 10, 1);

        for(FindAllReportHistoryWithPagingResult result : results1) {
            reportManagementService.updateReasonAndBanType(result.pk(), "사기", BanType.LOGIN_BAN_14D);
        }

        List< FindAllReportHistoryWithPagingResult> results2 = reportManagementService.findAllReportHistoryWithPaging(1, 10, 1);

        for(FindAllReportHistoryWithPagingResult result : results2) {
            assertThat(result.banType()).isEqualTo(BanType.LOGIN_BAN_14D);
            assertThat(result.reason()).isEqualTo("사기");
        }


    }

    @Test
    @DisplayName("신고 완료 시 제재 생성 (WARN 제외)")
    void completeReport_createsActiveSanction() {
        // given
        ReportEntity report = reportRepository.save(
                ReportEntity.builder()
                        .reporterUserPk(1L)
                        .targetUserPk(2L)
                        .reportUrl("url")
                        .reportContent("욕설")
                        .reportType(ReportType.ABUSE)
                        .banType(BanType.LOGIN_BAN_7D)
                        .reason("욕설")
                        .reportStatus(ReportStatus.IN_PROGRESS)
                        .build()
        );

        // when
        reportManagementService.updateReportStatus(
                report.getPk(),
                ReportStatus.COMPLETED
        );





        // then
        List<ActiveSanctionEntity> sanctions =
                activeSanctionJpaRepository.findAll();



        ActiveSanctionEntity sanction = sanctions.getFirst();
        assertThat(sanction.getTargetUserPk()).isEqualTo(2L);
        assertThat(sanction.getEndDateTime()).isAfter(sanction.getStartDateTime());

        System.out.println("start "+ sanction.getStartDateTime() + "end " + sanction.getEndDateTime());
    }

}