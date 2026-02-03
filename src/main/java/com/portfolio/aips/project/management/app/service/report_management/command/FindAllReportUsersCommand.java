package com.portfolio.aips.project.management.app.service.report_management.command;

import com.portfolio.aips.project.interaction.common.enums.BoardType;
import com.portfolio.aips.project.interaction.report.domain.model.ReportStatus;
import com.portfolio.aips.project.interaction.report.domain.model.ReportType;

import javax.annotation.Nullable;

public record FindAllReportUsersCommand(int page, int size, ReportStatus reportStatus, @Nullable ReportType reportType, @Nullable BoardType boardType) {
}
