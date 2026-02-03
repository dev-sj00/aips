package com.portfolio.aips.project.statistics.app.service.result;

import com.portfolio.aips.project.interaction.report.domain.model.ReportType;

public record FindAllStatisticsResult(ReportType reportType, Double percentage) {
}
