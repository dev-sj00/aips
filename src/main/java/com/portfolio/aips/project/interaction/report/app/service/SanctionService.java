package com.portfolio.aips.project.interaction.report.app.service;

import com.portfolio.aips.project.interaction.report.domain.model.BanType;
import com.portfolio.aips.project.interaction.report.domain.model.ReportStatus;
import com.portfolio.aips.project.interaction.report.app.service.command.CreateReportCommand;
import com.portfolio.aips.project.interaction.report.app.service.result.FindAllSanctionHistoryWithPagingResult;
import com.portfolio.aips.project.interaction.report.app.service.result.FindAllSanctionUsersWithPagingResult;

import java.util.List;

public interface SanctionService {
    void createReport(CreateReportCommand command);
    List<FindAllSanctionUsersWithPagingResult> findAllSanctionUsersWithPaging(int page, int size, ReportStatus reportStatus);
    List<FindAllSanctionHistoryWithPagingResult> findAllSanctionHistoryWithPaging(int page, int pageSize, long userPk);
    void updateReportStatus(long sanctionPk, ReportStatus reportStatus);
    void updateReasonAndBanType(long sanctionPk, String reason, BanType banType);

}
