package com.portfolio.aips.project.interaction.report.app.service;


import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;
import com.portfolio.aips.project.interaction.report.domain.entity.QSanctionEntity;
import com.portfolio.aips.project.interaction.report.domain.entity.SanctionEntity;
import com.portfolio.aips.project.interaction.report.domain.model.BanType;
import com.portfolio.aips.project.interaction.report.domain.model.ReportStatus;
import com.portfolio.aips.project.interaction.report.infra.SanctionRepository;
import com.portfolio.aips.project.interaction.report.app.service.command.CreateReportCommand;
import com.portfolio.aips.project.interaction.report.app.service.result.FindAllSanctionHistoryWithPagingResult;
import com.portfolio.aips.project.interaction.report.app.service.result.FindAllSanctionUsersWithPagingResult;

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
public class SanctionServiceImpl implements SanctionService {

    private final JPAQueryFactory queryFactory;
    private final SanctionRepository sanctionRepository;

    @Override
    public void createReport(CreateReportCommand command) {
        sanctionRepository.save(SanctionEntity
                .builder()
                .reporterUserPk(command.reporterUserPk())
                .targetUserPk(command.targetUserPk())
                .reportUrl(command.reportUrl())
                .reportContent(command.reportContent())
                .build());
    }

    @Override
    public List<FindAllSanctionUsersWithPagingResult> findAllSanctionUsersWithPaging(int page, int size, ReportStatus reportStatus) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("created_date_time").descending());

        QSanctionEntity s = QSanctionEntity.sanctionEntity;

        //pageable은 jpa 특성 상 fetch join 과 쓰면 안됨


        return queryFactory
                .select(Projections.constructor(FindAllSanctionUsersWithPagingResult.class, s.targetUser.nickname, s.targetUserPk.count()))
                .from(s)
                .groupBy(s.targetUserPk)
                .orderBy(s.targetUserPk.count().desc(), s.createdDateTime.max().desc())
                .where(s.reportStatus.eq(reportStatus))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<FindAllSanctionHistoryWithPagingResult> findAllSanctionHistoryWithPaging(int page, int size, long targetUserPk) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_date_time").descending());

        QSanctionEntity s = QSanctionEntity.sanctionEntity;


        return queryFactory
                .select(Projections.constructor(FindAllSanctionHistoryWithPagingResult.class,
                        s.pk, s.reportStatus, s.banType, s.reason, s.reportUrl,
                        s.reporterUser.nickname, s.reportContent, s.reportType, s.createdDateTime))
                .from(s)
                .where(s.targetUserPk.eq(targetUserPk))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();



    }

    @Override
    public void updateReportStatus(long sanctionPk, ReportStatus reportStatus) {
        QSanctionEntity r = QSanctionEntity.sanctionEntity;

        SanctionEntity reportEntity = queryFactory.selectFrom(r).where(r.pk.eq(sanctionPk)).fetchOne();


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
    public void updateReasonAndBanType(long sanctionPk, String reason, BanType banType) {

    }
}
