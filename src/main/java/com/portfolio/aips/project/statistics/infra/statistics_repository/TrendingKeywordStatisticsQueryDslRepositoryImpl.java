package com.portfolio.aips.project.statistics.infra.statistics_repository;

import com.portfolio.aips.project.search.archive.trending_search.domain.QTrendingSearchEntity;
import com.portfolio.aips.project.statistics.app.router.statistics_usecase.command.FindAllStatisticsCommand;
import com.portfolio.aips.project.statistics.app.router.statistics_usecase.result.FindAllTrendingKeywordStatisticsResult;
import com.portfolio.aips.project.statistics.domain.repo.StatisticsRepository;
import com.querydsl.core.group.GroupBy;
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

public class TrendingKeywordStatisticsQueryDslRepositoryImpl implements StatisticsRepository<FindAllStatisticsCommand, FindAllTrendingKeywordStatisticsResult> {

    private final JPAQueryFactory queryFactory;


    @Override
    public List<FindAllTrendingKeywordStatisticsResult> findAllStatisticsByCommand(FindAllStatisticsCommand command) {
        return List.of();
    }

    @Override
    public Page<FindAllTrendingKeywordStatisticsResult> findAllStatisticsByCommandWithOffsetAndLimit(FindAllStatisticsCommand command) {
        int page = command.page();
        int size = command.size();


        Pageable pageable = PageRequest.of(page, size);
        QTrendingSearchEntity q = QTrendingSearchEntity.trendingSearchEntity;

        String sortType = command.sortType().name().toLowerCase();


        DateTemplate<LocalDate> weekStart =
                Expressions.dateTemplate(
                        LocalDate.class,
                        "DATE_TRUNC('" + sortType + "', {0})::date",
                        q.createDateTime
                );



        List<LocalDate> weeks =
                queryFactory
                        .select(weekStart)
                        .from(q)
                        .groupBy(weekStart)
                        .orderBy(weekStart.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();


        List<FindAllTrendingKeywordStatisticsResult> content =
                queryFactory
                        .from(q)
                        .where(weekStart.in(weeks))
                        .orderBy(q.score.desc())
                        .transform(
                                GroupBy.groupBy(weekStart).list(
                                        Projections.constructor(
                                                FindAllTrendingKeywordStatisticsResult.class,
                                                weekStart,
                                                GroupBy.list(
                                                        Projections.constructor(
                                                                FindAllTrendingKeywordStatisticsResult.Item.class,
                                                                q.keyword,
                                                                q.docCount.coalesce(0L),
                                                                q.score.avg().coalesce(0.0)
                                                        )
                                                )
                                        )
                                )
                        );

        JPAQuery<Long> countQuery = queryFactory.select(weekStart.countDistinct()).from(q);


        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);

    }

    @Override
    public Class<FindAllStatisticsCommand> commandType() {
        return FindAllStatisticsCommand.class;
    }
}
