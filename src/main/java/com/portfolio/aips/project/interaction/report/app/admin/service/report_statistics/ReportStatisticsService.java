package com.portfolio.aips.project.interaction.report.app.admin.service.report_statistics;

import com.portfolio.aips.project.interaction.report.app.admin.service.report_statistics.command.FindAllStatisticsCommand;
import com.portfolio.aips.project.interaction.report.app.admin.service.report_statistics.result.FindAllStatisticsResult;

import java.util.List;

public interface ReportStatisticsService {
    List<FindAllStatisticsResult> findAllStatistics(FindAllStatisticsCommand command);
}
