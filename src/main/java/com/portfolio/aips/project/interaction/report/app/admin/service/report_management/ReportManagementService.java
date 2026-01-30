package com.portfolio.aips.project.interaction.report.app.admin.service.report_management;

import com.portfolio.aips.project.interaction.report.app.admin.service.report_management.command.FindAllReportUsersCommand;
import com.portfolio.aips.project.interaction.report.domain.model.BanType;
import com.portfolio.aips.project.interaction.report.domain.model.ReportStatus;
import com.portfolio.aips.project.interaction.report.app.admin.service.report_management.result.FindReportResult;
import com.portfolio.aips.project.interaction.report.app.admin.service.report_management.result.FindAllReportUsersResult;
import org.springframework.data.domain.Page;

public interface ReportManagementService {

    Page<FindAllReportUsersResult> findAllReportUsers(FindAllReportUsersCommand command);
    Page<FindReportResult> findAllReportHistory(int page, int pageSize, long userPk);


    void updateReportStatus(long reportPk, ReportStatus reportStatus);
    void updateReasonAndBanType(long reportPk, String reason, BanType banType);

}
