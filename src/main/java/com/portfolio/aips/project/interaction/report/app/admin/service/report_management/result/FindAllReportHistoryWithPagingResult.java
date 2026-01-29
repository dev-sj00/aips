package com.portfolio.aips.project.interaction.report.app.admin.service.report_management.result;

import com.portfolio.aips.project.interaction.report.domain.model.BanType;
import com.portfolio.aips.project.interaction.report.domain.model.ReportStatus;
import com.portfolio.aips.project.interaction.report.domain.model.ReportType;

import java.time.LocalDateTime;

public record FindAllReportHistoryWithPagingResult(Long pk,
                                                   ReportStatus reportStatus,
                                                   BanType banType,
                                                   String reason,
                                                   String reportUrl,
                                                   String reporterNickName,
                                                   String reportContent,
                                                   ReportType reportType,
                                                   LocalDateTime createdDateTime) {
}
