package com.portfolio.aips.project.interaction.report.app.user.service;

import com.portfolio.aips.project.interaction.report.app.user.service.command.CreateReportCommand;
import com.portfolio.aips.project.interaction.report.domain.entity.ReportEntity;
import com.portfolio.aips.project.interaction.report.infra.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateReportServiceImpl implements CreateReportService{

    private final ReportRepository reportRepository;

    @Override
    public void createReport(CreateReportCommand command) {
        reportRepository.save(ReportEntity
                .builder()

                .reporterUserPk(command.reporterUserPk())
                .targetUserPk(command.targetUserPk())
                .reportUrl(command.reportUrl())
                .reportType(command.reportType())
                .boardType(command.boardType())
                .reportContent(command.reportContent())
                .build());
    }
}
