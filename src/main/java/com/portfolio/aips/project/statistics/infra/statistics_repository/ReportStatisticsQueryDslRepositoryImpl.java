package com.portfolio.aips.project.statistics.infra.statistics_repository;

import com.portfolio.aips.project.interaction.common.enums.BoardType;
import com.portfolio.aips.project.statistics.app.router.statistics_usecase.command.FindAllReportStatisticsCommand;
import com.portfolio.aips.project.statistics.app.router.statistics_usecase.result.FindAllReportStatisticsResult;
import com.portfolio.aips.project.interaction.report.domain.entity.QReportEntity;
import com.portfolio.aips.project.interaction.report.domain.model.ReportStatus;
import com.portfolio.aips.project.statistics.domain.repo.StatisticsRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportStatisticsQueryDslRepositoryImpl implements StatisticsRepository<FindAllReportStatisticsCommand, FindAllReportStatisticsResult> {

    private final JPAQueryFactory queryFactory;



    private BooleanExpression findAllStatisticsCondition(FindAllReportStatisticsCommand command, QReportEntity q, LocalDateTime startDateTime, LocalDateTime endDateTime) {

        return q.reportStatus.ne(ReportStatus.CANCELLED)
                .and(q.createdDateTime.between(startDateTime, endDateTime))
                .and(hasBoardType(command.boardType(), q)
                );


    }

    private BooleanExpression hasBoardType(@Nullable BoardType boardType, QReportEntity q) {

        return boardType != null ? q.boardType.eq(boardType) : null;
    }



    @Override
    public List<FindAllReportStatisticsResult> findAllStatisticsByCommand(FindAllReportStatisticsCommand command) {

        QReportEntity r = QReportEntity.reportEntity;

        QReportEntity r2 = new QReportEntity("r2");



        LocalDateTime startDateTime = command.reportDateUnit().toLocalDateTime();
        LocalDateTime endDateTime = LocalDateTime.now();

        BooleanExpression mainCondition = findAllStatisticsCondition(command, r, startDateTime, endDateTime);
        BooleanExpression subCondition = findAllStatisticsCondition(command, r2, startDateTime, endDateTime);


        NumberExpression<Long> totalCount =

                Expressions.numberTemplate(
                        Long.class,
                        "({0})",
                        JPAExpressions
                                .select(r2.count())
                                .from(r2)
                                .where(subCondition)
                );


        NumberExpression<Double> percentage =
                Expressions.numberTemplate(
                        Double.class,
                        "round({0}, 2)",
                        Expressions.cases()
                                .when(totalCount.eq(0L)).then(0.0)
                                .otherwise(r.count().doubleValue()
                                        .divide(
                                                totalCount
                                        ).multiply(100)));


        return queryFactory.select(Projections.constructor(FindAllReportStatisticsResult.class, r.reportType, percentage))
                .from(r)
                .where(mainCondition)
                .groupBy(r.reportType)
                .fetch();


    }

    @Override
    public Page<FindAllReportStatisticsResult> findAllStatisticsByCommandWithOffsetAndLimit(FindAllReportStatisticsCommand command) {
        return null;
    }

    @Override
    public Class<FindAllReportStatisticsCommand> commandType() {
        return FindAllReportStatisticsCommand.class;
    }


}
