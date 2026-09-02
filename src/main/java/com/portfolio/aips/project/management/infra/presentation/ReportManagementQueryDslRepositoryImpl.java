package com.portfolio.aips.project.management.infra.presentation;


import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;

import com.portfolio.aips.project.interaction.common.enums.BoardType;
import com.portfolio.aips.project.management.app.service.report_management.command.FindAllReportUsersCommand;
import com.portfolio.aips.project.interaction.report.domain.entity.QReportEntity;
import com.portfolio.aips.project.interaction.report.domain.entity.ReportEntity;
import com.portfolio.aips.project.interaction.report.domain.model.BanType;
import com.portfolio.aips.project.interaction.report.domain.model.ReportStatus;
import com.portfolio.aips.project.management.app.service.report_management.result.FindReportResult;
import com.portfolio.aips.project.management.app.service.report_management.result.FindAllReportUsersResult;

import com.portfolio.aips.project.interaction.report.domain.model.ReportType;
import com.portfolio.aips.project.interaction.sanction.app.service.active_sanction.ActiveSanctionCommandService;
import com.portfolio.aips.project.management.domain.repo.ReportManagementRepository;
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
public class ReportManagementQueryDslRepositoryImpl implements ReportManagementRepository {

    private final JPAQueryFactory queryFactory;



    @Override
    public Page<FindAllReportUsersResult> findAllReportByCommandWithOffsetAndLimit(FindAllReportUsersCommand command) {

       int page = command.page();
       int size =  command.size();
       ReportStatus reportStatus = command.reportStatus();
       ReportType reportType = command.reportType();
       BoardType boardType = command.boardType();

       Pageable pageable = PageRequest.of(page, size);



        QReportEntity r = QReportEntity.reportEntity;

        //pageable은 jpa 특성 상 fetch join 과 쓰면 안됨


        JPAQuery<Long> totalCountQuery =  queryFactory
                .select(r.targetUserPk.countDistinct())
                .from(r)
                .where(r.reportStatus.eq(reportStatus));

        List<FindAllReportUsersResult> content = queryFactory
                .select(Projections.constructor(FindAllReportUsersResult.class, r.targetUserPk, r.targetUser.nickname, r.targetUserPk.count()))
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
    public Page<FindReportResult> findAllReportByUserPkWithOffsetAndLimit(int page, int size, long targetUserPk) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_date_time").descending());

        QReportEntity r = QReportEntity.reportEntity;


        List<FindReportResult> content = queryFactory
                .select(Projections.constructor(FindReportResult.class,
                        r.pk, r.reportStatus, r.banType, r.reason, r.reportUrl,
                        r.reporterUser.nickname, r.reportContent, r.reportType, r.createdDateTime))
                .from(r)
                .where(r.targetUserPk.eq(targetUserPk))
                .offset(pageable.getOffset())
                .orderBy(r.createdDateTime.desc())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> totalCountQuery =  queryFactory
                .select(r.targetUserPk.countDistinct())
                .from(r)
                .where(r.targetUserPk.eq(targetUserPk));

        return PageableExecutionUtils.getPage(content, pageable, totalCountQuery::fetchOne);



    }


    @Override
    public ReportEntity updateReportStatusByReportPk(long reportPk, ReportStatus reportStatus) {
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


        return reportEntity;






    }

    @Override
    public void updateReasonAndBanTypeByReportPk(long reportPk, String reason, BanType banType) {
        QReportEntity  r = QReportEntity.reportEntity;
        ReportEntity reportEntity = queryFactory.selectFrom(r).where(r.pk.eq(reportPk)).fetchOne();

        if(reportEntity == null){
            throw new RuntimeException("Sanction not found");
        }

        reportEntity.updateReasonAndBanType(reason, banType);

    }
}
