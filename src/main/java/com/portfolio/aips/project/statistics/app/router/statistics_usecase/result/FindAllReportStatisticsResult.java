package com.portfolio.aips.project.statistics.app.router.statistics_usecase.result;

import com.portfolio.aips.project.interaction.report.domain.model.ReportType;


public record FindAllReportStatisticsResult(ReportType reportType,
                                            Double percentage) {

}
