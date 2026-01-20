package com.portfolio.aips.project.interaction.rating.repo;

import com.portfolio.aips.project.interaction.enums.BoardType;
import com.portfolio.aips.project.interaction.rating.entity.RatingEntity;
import com.portfolio.aips.project.interaction.rating.repo.result.PopularityScoreElementsResult;
import com.portfolio.aips.project.interaction.rating.service.rating_scheduler.command.FindAllPopularityScoreElementsWithTempTableCommand;
import com.portfolio.aips.project.utils.BatchUtils;
import com.portfolio.aips.project.utils.virtual_thread_utils.BoundedExecutor;
import com.portfolio.aips.project.utils.virtual_thread_utils.VirtualThreadBoundedExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RatingBatchRepositoryImpl implements RatingBatchRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ExecutorService vtExecutor;
    private final Semaphore dbSemaphore;



    @Override
    public List<PopularityScoreElementsResult> FindAllPopularityScoreElementsWithTempTable(FindAllPopularityScoreElementsWithTempTableCommand command) throws InterruptedException {

        List<RatingEntity> ratingEntities = command.ratingEntities();
        int batchSize = command.batchSize();
        int tempBatchSize = command.batchSize();


        List<List<RatingEntity>> batches = BatchUtils.chunk(ratingEntities, batchSize);
        List< Future <List<PopularityScoreElementsResult>>> futures = new ArrayList<>();


        BoundedExecutor boundedExecutor = VirtualThreadBoundedExecutor
                .builder()
                .executor(vtExecutor)
                .semaphore(dbSemaphore)
                .timeout(5L,  TimeUnit.SECONDS)
                .build();

        for(List<RatingEntity> batch : batches) {

            Future <List<PopularityScoreElementsResult>> future = boundedExecutor
                    .submit(() -> executeAvgRatingBatch(batch, tempBatchSize));



            futures.add(future);

        }




        return VirtualThreadBoundedExecutor.join(futures);

    }

    private List<PopularityScoreElementsResult> executeAvgRatingBatch(
            List<RatingEntity> ratingEntities, int tempBatchSize) {

        // 1. 임시 테이블 생성
        jdbcTemplate.execute("""
        CREATE TEMP TABLE tmp_targets (
            board_type VARCHAR(20),
            board_pk BIGINT
        )
    """);

        // 2. batch insert
        jdbcTemplate.batchUpdate(
                "INSERT INTO tmp_targets (board_type, board_pk) VALUES (?, ?)",
                ratingEntities,
                tempBatchSize,
                (ps, target) -> {
                    ps.setString(1, target.getBoardType().name());
                    ps.setLong(2, target.getBoardPk());
                }
        );

        // 3. AVG 조회
        return jdbcTemplate.query("""
            
                        SELECT t.board_type, t.board_pk,
                   AVG(r.usefulness_score) AS usefulness_avg_score,
                   AVG(r.reliability_score) AS reliability_avg_score,
                   AVG(r.fun_score) AS fun_avg_score,
                   COUNT(*) AS rating_count,
                   v.view_count AS view_count
            FROM tmp_targets t
            JOIN rating r
              ON r.board_type = t.board_type
             AND r.board_pk = t.board_pk
            JOIN view v
            ON v.board_type = t.board_type
            AND v.board_pk = t.board_pk
            GROUP BY t.board_type, t.board_pk, v.view_count
            """,
                (rs, rowNum) -> new PopularityScoreElementsResult(
                        BoardType.valueOf(rs.getString("board_type")),
                        rs.getLong("board_pk"),
                        rs.getDouble("usefulness_avg_score"),
                        rs.getDouble("reliability_avg_score"),
                        rs.getDouble("fun_avg_score"),
                        rs.getLong("rating_count"),
                        rs.getLong("view_count")
                )
        );
        }


}
