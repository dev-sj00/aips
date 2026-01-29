package com.portfolio.aips.project.interaction.report.app.admin.service.report_management;


import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;

import com.portfolio.aips.project.interaction.common.enums.BoardType;
import com.portfolio.aips.project.interaction.report.app.admin.service.report_management.command.FindAllReportUsersCommand;
import com.portfolio.aips.project.interaction.report.domain.entity.QReportEntity;
import com.portfolio.aips.project.interaction.report.domain.entity.ReportEntity;
import com.portfolio.aips.project.interaction.report.domain.event.ReportStatusCompletedEvent;
import com.portfolio.aips.project.interaction.report.domain.model.BanType;
import com.portfolio.aips.project.interaction.report.domain.model.ReportStatus;
import com.portfolio.aips.project.interaction.report.app.admin.service.report_management.result.FindAllReportHistoryWithPagingResult;
import com.portfolio.aips.project.interaction.report.app.admin.service.report_management.result.FindAllReportUsersWithPagingResult;

import com.portfolio.aips.project.interaction.report.domain.model.ReportType;
import com.portfolio.aips.project.interaction.sanction.app.ActiveSanctionService;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportManagementServiceImpl implements ReportManagementService {

    private final JPAQueryFactory queryFactory;

    private final ActiveSanctionService activeSanctionService;
    private final ApplicationEventPublisher applicationEventPublisher;



    @Override
    public Page<FindAllReportUsersWithPagingResult> findAllReportUsers(FindAllReportUsersCommand command) {

       int page = command.page();
       int size =  command.size();
       ReportStatus reportStatus = command.reportStatus();
       ReportType reportType = command.reportType();
       BoardType boardType = command.boardType();

       Pageable pageable = PageRequest.of(page, size, Sort.by("created_date_time").descending());



        QReportEntity r = QReportEntity.reportEntity;

        //pageable은 jpa 특성 상 fetch join 과 쓰면 안됨


        JPAQuery<Long> totalCountQuery =  queryFactory
                .select(r.targetUserPk.countDistinct())
                .from(r)
                .where(r.reportStatus.eq(reportStatus));

        List<FindAllReportUsersWithPagingResult> content = queryFactory
                .select(Projections.constructor(FindAllReportUsersWithPagingResult.class, r.targetUserPk, r.targetUser.nickname, r.targetUserPk.count()))
                .from(r)
                .groupBy(r.targetUserPk, r.targetUser.nickname)
                .orderBy(r.targetUserPk.count().desc(), r.createdDateTime.max().desc())
                .where(r.reportStatus.eq(reportStatus), isContainReportType(r, reportType), isContainBoardType(r, boardType))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        //groupBy를 썻기 때문에 자체 sort 기능과 결과값이 다름

        return PageableExecutionUtils.getPage(content, pageable, totalCountQuery::fetchOne);
    }

    private BooleanExpression isContainReportType(QReportEntity r, ReportType reportType) {
        return reportType != null ?  r.reportType.eq(reportType) : null;
    }

    private BooleanExpression isContainBoardType(QReportEntity r, BoardType boardType) {
        return boardType != null ? r.boardType.eq(boardType) : null;
    }


    @Override
    public Page<FindAllReportHistoryWithPagingResult> findAllReportHistory(int page, int size, long targetUserPk) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_date_time").descending());

        QReportEntity r = QReportEntity.reportEntity;


        List<FindAllReportHistoryWithPagingResult> content = queryFactory
                .select(Projections.constructor(FindAllReportHistoryWithPagingResult.class,
                        r.pk, r.reportStatus, r.banType, r.reason, r.reportUrl,
                        r.reporterUser.nickname, r.reportContent, r.reportType, r.createdDateTime))
                .from(r)
                .where(r.targetUserPk.eq(targetUserPk))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> totalCountQuery =  queryFactory
                .select(r.targetUserPk.countDistinct())
                .from(r)
                .where(r.targetUserPk.eq(targetUserPk));

        return PageableExecutionUtils.getPage(content, pageable, totalCountQuery::fetchOne);



    }

    @Override
    @Transactional
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

        if(reportEntity.getReportStatus() == ReportStatus.COMPLETED)
        {
            activeSanctionService.createActiveSanction
                    (reportEntity.getBanType(),
                     reportEntity.getTargetUserPk());
        }

        reportEntity.updateStatusCompleted(); //이벤트 생성

        applicationEventPublisher.publishEvent //이벤트 발행
                (new ReportStatusCompletedEvent
                (reportEntity.getReason(),
                reportEntity.getTargetUserPk()));


    }

    @Override
    public void updateReasonAndBanType(long reportPk, String reason, BanType banType) {
        QReportEntity  r = QReportEntity.reportEntity;
        ReportEntity reportEntity = queryFactory.selectFrom(r).where(r.pk.eq(reportPk)).fetchOne();

        if(reportEntity == null){
            throw new RuntimeException("Sanction not found");
        }

        reportEntity.updateReasonAndBanType(reason, banType);

    }
}
