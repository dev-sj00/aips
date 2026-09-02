package com.portfolio.aips.project.management.app.usecase.command;

import com.portfolio.aips.project.interaction.report.domain.model.ReportStatus;

public record UpdateReportStatusIfCompletedPublishEventCommand(Long reportPk, ReportStatus reportStatus) {
}
