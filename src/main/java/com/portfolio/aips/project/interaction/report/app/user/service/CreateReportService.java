package com.portfolio.aips.project.interaction.report.app.user.service;

import com.portfolio.aips.project.interaction.report.app.user.service.command.CreateReportCommand;

public interface CreateReportService {
    void createReport(CreateReportCommand command);
}
