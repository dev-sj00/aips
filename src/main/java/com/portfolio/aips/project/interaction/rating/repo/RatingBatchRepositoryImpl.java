package com.portfolio.aips.project.interaction.rating.repo;

import com.portfolio.aips.project.interaction.rating.entity.RatingEntity;
import com.portfolio.aips.project.interaction.rating.repo.result.BoardAvgRatingScoreResult;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RatingBatchRepositoryImpl implements RatingBatchRepository {

    private final JdbcTemplate jdbcTemplate;




    @Override
    public List<BoardAvgRatingScoreResult> findAllAvgRatingScoresWithTempTable(List<RatingEntity> batch, int batchSize) {
        throw new UnsupportedOperationException("Virtual Thread 작업 해야함");

        jdbcTemplate.execute("""
            CREATE TEMP TABLE tmp_targets
                   (
                   board_type VARCHAR(20),
                   board_pk BIGINT,
                   )
        """);

        jdbcTemplate.batchUpdate("INSERT INTO tmp_targets (board_type, board_pk) VALUES (?, ?)"
                ,batch,
                batchSize,
                (ps, target) -> {
                    ps.setString(1, target.getBoardType().name());
                    ps.setLong(2, target.getBoardPk());
                }
                );


        return jdbcTemplate.query(
                """
                  SELECT t.board_type, t.board_pk,
                         AVG(r.usefulness_score) AS usefulness_avg_score,
                         AVG(r.reliability_score) AS  reliability_avg_score,
                         AVG(r.fun_score) AS fun_avg_score
                  FROM tmp_targets t
                  JOIN rating r 
                  ON r.board_type = t.board_type
                  AND r.board_pk = t.board_pk
                  GROUP BY t.board_type, t.board_pk
                """,
                (rs, rowNum) -> new BoardAvgRatingScoreResult(
                        rs.getString("board_type"),
                        rs.getLong("board_pk"),
                        rs.getDouble("usefulness_avg_score"),
                        rs.getDouble("reliability_avg_score"),
                        rs.getDouble("fun_avg_score")
                )
        );


    }
}
