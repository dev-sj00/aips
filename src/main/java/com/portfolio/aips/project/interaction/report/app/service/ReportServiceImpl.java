package com.portfolio.aips.project.interaction.report.app.service;


import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;

import com.portfolio.aips.project.interaction.report.domain.entity.QReportEntity;
import com.portfolio.aips.project.interaction.report.domain.entity.ReportEntity;
import com.portfolio.aips.project.interaction.report.domain.model.BanType;
import com.portfolio.aips.project.interaction.report.domain.model.ReportStatus;
import com.portfolio.aips.project.interaction.report.infra.ReportRepository;
import com.portfolio.aips.project.interaction.report.app.service.command.CreateReportCommand;
import com.portfolio.aips.project.interaction.report.app.service.result.FindAllReportHistoryWithPagingResult;
import com.portfolio.aips.project.interaction.report.app.service.result.FindAllReportUsersWithPagingResult;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final JPAQueryFactory queryFactory;
    private final ReportRepository reportRepository;

    @Override
    public void createReport(CreateReportCommand command) {
        reportRepository.save(ReportEntity
                .builder()
                .reporterUserPk(command.reporterUserPk())
                .targetUserPk(command.targetUserPk())
                .reportUrl(command.reportUrl())
                .reportContent(command.reportContent())
                .build());
    }

    @Override
    public List<FindAllReportUsersWithPagingResult> findAllReportUsersWithPaging(int page, int size, ReportStatus reportStatus) {

       Pageable pageable = PageRequest.of(page, size, Sort.by("created_date_time").descending());

        QReportEntity r = QReportEntity.reportEntity;

        //pageable은 jpa 특성 상 fetch join 과 쓰면 안됨


        return queryFactory
                .select(Projections.constructor(FindAllReportUsersWithPagingResult.class, r.targetUser.nickname, r.targetUserPk.count()))
                .from(r)
                .groupBy(r.targetUserPk)
                .orderBy(r.targetUserPk.count().desc(), r.createdDateTime.max().desc())
                .where(r.reportStatus.eq(reportStatus))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<FindAllReportHistoryWithPagingResult> findAllReportHistoryWithPaging(int page, int size, long targetUserPk) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_date_time").descending());

        QReportEntity r = QReportEntity.reportEntity;


        return queryFactory
                .select(Projections.constructor(FindAllReportHistoryWithPagingResult.class,
                        r.pk, r.reportStatus, r.banType, r.reason, r.reportUrl,
                        r.reporterUser.nickname, r.reportContent, r.reportType, r.createdDateTime))
                .from(r)
                .where(r.targetUserPk.eq(targetUserPk))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();



    }

    @Override
    public void updateReportStatus(long reportPk, ReportStatus reportStatus) {
        QReportEntity r = QReportEntity.reportEntity;

        ReportEntity reportEntity = queryFactory.selectFrom(r).where(r.pk.eq(reportPk)).fetchOne();


        if(reportEntity == null){
            throw new RuntimeException("Sanction not found");
        }

        if(reportEntity.StatusIsNotValid())
        {
            throw new CustomException(ErrorCode.NOT_FOUND_REASON_OR_BAN_TYPE);
        }


        reportEntity.updateStatus(reportStatus);
        reportEntity.updateStatusCompleted();



    }

    @Override
    public void updateReasonAndBanType(long reportPk, String reason, BanType banType) {

    }
}
