package com.portfolio.aips.project.visitor.infra.persistence;

import com.portfolio.aips.project.visitor.domain.entity.QVisitorEntity;
import com.portfolio.aips.project.visitor.domain.vo.FindAllVisitorStatisticsResultVO;
import com.portfolio.aips.project.visitor.domain.vo.FindAllVisitorStatisticsVO;
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
public class VisitorStatisticsQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    Page<FindAllVisitorStatisticsResultVO> findAllBySortType(FindAllVisitorStatisticsVO vo)
    {

        Pageable pageable = PageRequest.of(vo.page(), vo.size());
        QVisitorEntity q = QVisitorEntity.visitorEntity;



        //week, month
        String sortType = vo.sortType().name().toLowerCase();

        // 날짜를 그 주의 '월요일'로 자름
        // 예: 1월 3일(수) -> 1월 1일(월)로 변환됨
        DateTemplate<LocalDate> weekStart = Expressions.dateTemplate(
                LocalDate.class,
                "CAST(DATE_TRUNC('"+sortType+"', {0}) AS date)",
                q.createdDate
        );



        List<FindAllVisitorStatisticsResultVO> content = queryFactory.select(Projections.constructor(FindAllVisitorStatisticsResultVO.class, weekStart, q.visitCount.sumLong().coalesce(0L))).from(q)
                .groupBy(weekStart).orderBy(weekStart.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();



        JPAQuery<Long> countQuery = queryFactory.select(weekStart.countDistinct()).from(q);



        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);

    }
}
