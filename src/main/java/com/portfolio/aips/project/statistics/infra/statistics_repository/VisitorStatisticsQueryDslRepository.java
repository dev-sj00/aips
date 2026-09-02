package com.portfolio.aips.project.statistics.infra.statistics_repository;

import com.portfolio.aips.project.statistics.domain.repo.StatisticsRepository;
import com.portfolio.aips.project.visitor.domain.entity.QVisitorEntity;
import com.portfolio.aips.project.statistics.app.router.statistics_usecase.result.FindAllVisitorStatisticsResult;
import com.portfolio.aips.project.statistics.app.router.statistics_usecase.command.FindAllStatisticsCommand;
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
public class VisitorStatisticsQueryDslRepository implements StatisticsRepository<FindAllStatisticsCommand, FindAllVisitorStatisticsResult> {

    private final JPAQueryFactory queryFactory;




    @Override
    public List<FindAllVisitorStatisticsResult> findAllStatisticsByCommand(FindAllStatisticsCommand command) {
        return List.of();
    }

    @Override
    public Page<FindAllVisitorStatisticsResult> findAllStatisticsByCommandWithOffsetAndLimit(FindAllStatisticsCommand command) {
        int page = command.page();
        int size = command.size();


        Pageable pageable = PageRequest.of(page, size);
        QVisitorEntity q = QVisitorEntity.visitorEntity;



        //week, month
        String sortType = command.sortType().name().toLowerCase();

        // 날짜를 그 주의 '월요일'로 자름
        // 예: 1월 3일(수) -> 1월 1일(월)로 변환됨
        DateTemplate<LocalDate> weekStart = Expressions.dateTemplate(
                LocalDate.class,
                "CAST(DATE_TRUNC('"+sortType+"', {0}) AS date)",
                q.createdDate
        );





        List<FindAllVisitorStatisticsResult> content = queryFactory.select(Projections.constructor(FindAllVisitorStatisticsResult.class, weekStart, q.visitCount.sumLong().coalesce(0L)))
                .from(q)
                .groupBy(weekStart).orderBy(weekStart.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();



        JPAQuery<Long> countQuery = queryFactory.select(weekStart.countDistinct()).from(q);


        return PageableExecutionUtils.getPage( content, pageable, countQuery::fetchOne);
    }

    @Override
    public Class<FindAllStatisticsCommand> commandType() {
        return FindAllStatisticsCommand.class;
    }


}
