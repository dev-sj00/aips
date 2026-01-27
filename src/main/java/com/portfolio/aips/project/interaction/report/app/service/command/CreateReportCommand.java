package com.portfolio.aips.project.interaction.report.app.service.command;


public record CreateReportCommand(

        String reportUrl,
        Long targetUserPk,

        Long reporterUserPk,

        String reportContent

) {

}
