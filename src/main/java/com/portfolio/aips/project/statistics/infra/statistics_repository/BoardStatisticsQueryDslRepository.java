package com.portfolio.aips.project.statistics.infra.statistics_repository;

import com.portfolio.aips.project.statistics.app.router.statistics_usecase.command.FindAllStatisticsCommand;
import com.portfolio.aips.project.statistics.app.router.statistics_usecase.result.FindAllBoardStatisticsResult;
import com.portfolio.aips.project.statistics.domain.entity.QBoardStatisticsEntity;
import com.portfolio.aips.project.statistics.domain.repo.StatisticsRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
@RequiredArgsConstructor
public class BoardStatisticsQueryDslRepository implements StatisticsRepository<FindAllStatisticsCommand, FindAllBoardStatisticsResult> {

    private final JPAQueryFactory queryFactory;




    @Override
    public List<FindAllBoardStatisticsResult> findAllStatisticsByCommand(FindAllStatisticsCommand command) {
        return List.of();
    }

    @Override
    public Page<FindAllBoardStatisticsResult> findAllStatisticsByCommandWithOffsetAndLimit(FindAllStatisticsCommand command) {

        QBoardStatisticsEntity q = QBoardStatisticsEntity.boardStatisticsEntity;

        Pageable pageAble = PageRequest.of(command.page(), command.size());


        //week, month
        String sortType = command.sortType().name().toLowerCase();



        DateTemplate<LocalDate> weekStart = Expressions.dateTemplate(
                LocalDate.class,
                "CAST(DATE_TRUNC('"+sortType+"', {0}) AS date)",
                q.createdDate
        );


        List<FindAllBoardStatisticsResult> content = queryFactory.select(
                Projections.constructor(FindAllBoardStatisticsResult.class,
                        weekStart.stringValue(),
                        q.submitCount.sumLong())
                )
                .from(q)
                .groupBy(weekStart)
                .offset(pageAble.getOffset())
                .orderBy(weekStart.desc())
                .limit(pageAble.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery =  queryFactory.select(weekStart.countDistinct()).from(q);


        return PageableExecutionUtils.getPage(content, pageAble, countQuery::fetchOne);

        
    }

    @Override
    public Class<FindAllStatisticsCommand> commandType() {
        return FindAllStatisticsCommand.class;
    }
}
