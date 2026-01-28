package com.portfolio.aips.project.interaction.report.app.user.service.command;


import com.portfolio.aips.project.interaction.report.domain.model.ReportType;

public record CreateReportCommand(

        String reportUrl,
        Long targetUserPk,

        Long reporterUserPk,
        ReportType reportType,
        String reportContent

) {

}
