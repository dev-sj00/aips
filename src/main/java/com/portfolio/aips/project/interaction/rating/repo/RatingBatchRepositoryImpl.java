package com.portfolio.aips.project.interaction.rating.repo;

import com.portfolio.aips.project.interaction.common.enums.BoardType;
import com.portfolio.aips.project.interaction.rating.repo.result.FindAllWithScanResult;
import com.portfolio.aips.project.interaction.rating.repo.result.PopularityScoreElementsResult;
import com.portfolio.aips.project.interaction.rating.service.rating_scheduler.command.FindAllPopularityScoreElementsWithTempTableCommand;
import com.portfolio.aips.project.utils.BatchUtils;
import com.portfolio.aips.project.utils.virtual_thread_utils.BoundedExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RatingBatchRepositoryImpl implements RatingBatchRepository {

    private final JdbcTemplate jdbcTemplate;
    private final BoundedExecutor dbBoundedExecutor;
    private final TransactionTemplate transactionTemplate;




    @Override
    @Transactional
    public List<PopularityScoreElementsResult> FindAllPopularityScoreElementsWithTempTable(FindAllPopularityScoreElementsWithTempTableCommand command)  {

        List<FindAllWithScanResult> findAllWithScanResults = command.findAllWithScanResults();
        int batchSize = command.batchSize();
        int tempBatchSize = command.batchSize();


        List<List<FindAllWithScanResult>> batches = BatchUtils.chunk(findAllWithScanResults, batchSize);
        List< Future <List<PopularityScoreElementsResult>>> futures = new ArrayList<>();




        for(List<FindAllWithScanResult> batch : batches) {

            Future <List<PopularityScoreElementsResult>> future = dbBoundedExecutor
                    .submit(() -> executeAvgRatingBatch(batch));

            futures.add(future);

        }




        return dbBoundedExecutor.join(futures);

    }


    private List<PopularityScoreElementsResult> executeAvgRatingBatch(
            List<FindAllWithScanResult> findAllWithScanResults) {

        // 1. 임시 테이블 생성

        String sql = """
            SELECT
                t.board_type,
                t.board_pk,
                t.created_date_time,
                AVG(r.usefulness_score)  AS usefulness_avg_score,
                AVG(r.reliability_score) AS reliability_avg_score,
                AVG(r.fun_score)         AS fun_avg_score,
                COUNT(*)                 AS rating_count,
                v.view_count
            FROM unnest(
                    ?::varchar[],
                    ?::bigint[],
                    ?::timestamp[]
                 ) AS t(board_type, board_pk, created_date_time)
            JOIN rating r
              ON r.board_type = t.board_type
             AND r.board_pk   = t.board_pk
            JOIN view v
              ON v.board_type = t.board_type
             AND v.board_pk   = t.board_pk
            GROUP BY 
                t.board_type, 
                t.board_pk, 
                v.view_count, 
                t.created_date_time;
        """;




        return jdbcTemplate.query(con -> {

            PreparedStatement ps = con.prepareStatement(sql);

            String[] boardTypes = findAllWithScanResults.stream()
                    .map(r -> r.boardType().name())
                    .toArray(String[]::new);

            Long[] boardPks = findAllWithScanResults.stream()
                    .map(FindAllWithScanResult::boardPk)
                    .toArray(Long[]::new);

            Timestamp[] createdTimes = findAllWithScanResults.stream()
                    .map(r -> Timestamp.valueOf(r.createdDateTime()))
                    .toArray(Timestamp[]::new);


            log.info("boardTypes {} boardPks: {}, createTimes {}", boardTypes, boardPks, createdTimes);
            ps.setArray(1, con.createArrayOf("varchar", boardTypes));
            ps.setArray(2, con.createArrayOf("bigint", boardPks));
            ps.setArray(3, con.createArrayOf("timestamp", createdTimes));

            return ps;

        }, (rs, rowNum) -> new PopularityScoreElementsResult(
                BoardType.valueOf(rs.getString("board_type")),
                rs.getLong("board_pk"),
                rs.getDouble("usefulness_avg_score"),
                rs.getDouble("reliability_avg_score"),
                rs.getDouble("fun_avg_score"),
                rs.getLong("rating_count"),
                rs.getLong("view_count"),
                rs.getObject("created_date_time", LocalDateTime.class)
        ));
        }


}
