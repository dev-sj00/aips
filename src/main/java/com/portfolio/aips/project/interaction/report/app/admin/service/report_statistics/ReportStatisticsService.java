package com.portfolio.aips.project.interaction.report.app.admin.service.report_statistics;

import com.portfolio.aips.project.interaction.report.app.admin.service.report_statistics.command.FindAllStatisticsCommand;

public interface ReportStatisticsService {
    void findAllStatistics(FindAllStatisticsCommand command);
}
