package com.portfolio.aips.project.management.app.service.report_management;

import com.portfolio.aips.project.interaction.report.domain.model.BanType;
import com.portfolio.aips.project.management.app.service.report_management.command.FindAllReportUsersCommand;
import com.portfolio.aips.project.management.app.service.report_management.result.FindAllReportUsersResult;
import com.portfolio.aips.project.management.app.service.report_management.result.FindReportResult;
import org.springframework.data.domain.Page;

public interface ReportManagementService {
    Page<FindAllReportUsersResult> findAllReportProc(FindAllReportUsersCommand command);
    Page<FindReportResult> findAllReportProc(int page, int pageSize, long userPk);

    void updateReasonAndBanTypeProc(long reportPk, String reason, BanType banType);

}
