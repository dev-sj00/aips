package com.portfolio.aips.project.interaction.report.app.admin.service.report_statistics.result;

import com.portfolio.aips.project.interaction.report.domain.model.ReportType;

public record FindAllStatisticsResult(ReportType reportType, Double percentage) {
}
