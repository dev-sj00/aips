package com.portfolio.aips.project.interaction.report.app.admin.service.report_statistics;

import com.portfolio.aips.project.interaction.common.enums.BoardType;
import com.portfolio.aips.project.interaction.report.app.admin.service.report_statistics.command.FindAllStatisticsCommand;
import com.portfolio.aips.project.interaction.report.app.admin.service.report_statistics.result.FindAllStatisticsResult;
import com.portfolio.aips.project.interaction.report.app.user.service.CreateReportService;
import com.portfolio.aips.project.interaction.report.app.user.service.command.CreateReportCommand;
import com.portfolio.aips.project.interaction.report.domain.model.ReportDateUnit;
import com.portfolio.aips.project.interaction.report.domain.model.ReportType;
import com.portfolio.aips.project.interaction.report.infra.ReportRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ReportStatisticsSpringBootTest {

    @Autowired
    private CreateReportService createReportService;

    @Autowired
    private ReportStatisticsService reportStatisticsService;

    @Autowired
    private EntityManager em;



    private void create(ReportType reportType) {
        createReportService.createReport(
                new CreateReportCommand(
                        "https://test.url",
                        BoardType.Archive,
                        2L,     // targetUserPk
                        1L,     // reporterUserPk
                        reportType,
                        "통계 테스트용 신고"
                )
        );
    }

    @Test
    void 통계_percentage_정상_계산된다() {
        Map<ReportType, Integer> counts = Map.of(
                ReportType.SPAM, 15,
                ReportType.ABUSE, 20,
                ReportType.SEXUAL_CONTENT, 10,
                ReportType.VIOLENCE_CONTENT, 10,
                ReportType.ILLEGAL_CONTENT, 10,
                ReportType.FRAUD, 10,
                ReportType.COPYRIGHT, 5,
                ReportType.PERSONAL_INFO, 5,
                ReportType.ETC, 15
        );

        counts.forEach((type, count) -> {
            for (int i = 0; i < count; i++) {
                create(type);
            }
        });


        em.flush();
        em.clear();

        FindAllStatisticsCommand command =
                new FindAllStatisticsCommand(
                        ReportDateUnit.WEEK,
                        BoardType.Archive
                );

        // when
        List<FindAllStatisticsResult> results =
                reportStatisticsService.findAllStatistics(command);

        // then
        // then
        assertThat(results).hasSize(9);

        Map<ReportType, Double> map = results.stream()
                .collect(Collectors.toMap(
                        FindAllStatisticsResult::reportType,
                        FindAllStatisticsResult::percentage
                ));

        assertThat(map.get(ReportType.SPAM)).isCloseTo(15.00, within(0.01));
        assertThat(map.get(ReportType.ABUSE)).isCloseTo(20.00, within(0.01));
        assertThat(map.get(ReportType.SEXUAL_CONTENT)).isCloseTo(10.00, within(0.01));
        assertThat(map.get(ReportType.VIOLENCE_CONTENT)).isCloseTo(10.00, within(0.01));
        assertThat(map.get(ReportType.ILLEGAL_CONTENT)).isCloseTo(10.00, within(0.01));
        assertThat(map.get(ReportType.FRAUD)).isCloseTo(10.00, within(0.01));
        assertThat(map.get(ReportType.COPYRIGHT)).isCloseTo(5.00, within(0.01));
        assertThat(map.get(ReportType.PERSONAL_INFO)).isCloseTo(5.00, within(0.01));
        assertThat(map.get(ReportType.ETC)).isCloseTo(15.00, within(0.01));
    }

    }
