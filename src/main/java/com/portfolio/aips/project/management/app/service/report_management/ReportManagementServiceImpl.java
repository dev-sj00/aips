package com.portfolio.aips.project.management.app.service.report_management;

import com.portfolio.aips.project.interaction.report.domain.model.BanType;
import com.portfolio.aips.project.management.app.service.report_management.command.FindAllReportUsersCommand;
import com.portfolio.aips.project.management.app.service.report_management.result.FindAllReportUsersResult;
import com.portfolio.aips.project.management.app.service.report_management.result.FindReportResult;
import com.portfolio.aips.project.management.domain.repo.ReportManagementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportManagementServiceImpl implements ReportManagementService {

    private final ReportManagementRepository reportManagementRepository;

    @Override
    public Page<FindAllReportUsersResult> findAllReportProc(FindAllReportUsersCommand command) {
        return reportManagementRepository.findAllReportByCommandWithOffsetAndLimit(command);
    }

    @Override
    public Page<FindReportResult> findAllReportProc(int page, int pageSize, long userPk) {
        return reportManagementRepository.findAllReportByUserPkWithOffsetAndLimit(page, pageSize, userPk);
    }

    @Override
    public void updateReasonAndBanTypeProc(long reportPk, String reason, BanType banType) {
        reportManagementRepository.updateReasonAndBanTypeByReportPk(reportPk, reason, banType);
    }
}
