package com.portfolio.aips.project.interaction.report.app.admin.service;

import com.portfolio.aips.project.interaction.report.domain.model.BanType;
import com.portfolio.aips.project.interaction.report.domain.model.ReportStatus;
import com.portfolio.aips.project.interaction.report.app.admin.service.result.FindAllReportHistoryWithPagingResult;
import com.portfolio.aips.project.interaction.report.app.admin.service.result.FindAllReportUsersWithPagingResult;

import java.util.List;

public interface ReportManagementService {

    List<FindAllReportUsersWithPagingResult> findAllReportUsersWithPaging(int page, int size, ReportStatus reportStatus);
    List<FindAllReportHistoryWithPagingResult> findAllReportHistoryWithPaging(int page, int pageSize, long userPk);
    void updateReportStatus(long reportPk, ReportStatus reportStatus);
    void updateReasonAndBanType(long reportPk, String reason, BanType banType);

}
