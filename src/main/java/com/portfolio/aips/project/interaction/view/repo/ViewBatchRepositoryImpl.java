package com.portfolio.aips.project.interaction.view.repo;

import com.portfolio.aips.project.interaction.enums.BoardType;
import com.portfolio.aips.project.interaction.view.entity.ViewEntity;
import com.portfolio.aips.project.interaction.view.repo.dto.result.FindAllViewBatchProcResult;
import com.portfolio.aips.project.utils.BatchUtils;
import com.portfolio.aips.project.utils.virtual_thread_utils.BoundedExecutor;
import com.portfolio.aips.project.utils.virtual_thread_utils.VirtualThreadBoundedExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ViewBatchRepositoryImpl implements ViewBatchRepository {
    private final JdbcTemplate jdbcTemplate;
    private final BoundedExecutor dbBoundedExecutor;


    @Override
    public void     updateViewBatchProc(List<ViewEntity> viewEntities,  int batchSize)  {
        String sql = "UPDATE view SET view_count = view_count + ? WHERE board_pk = ? AND board_type = ?";



        List<List<ViewEntity>> batches = BatchUtils.chunk(viewEntities, 1000);



        for(List<ViewEntity> batch : batches) {
            dbBoundedExecutor
                    .execute(executeUpdateViewBatchProc(sql, batch));
        }


    }

    @Override
    public List<FindAllViewBatchProcResult> findAllViewBatchProc(List<ViewEntity> viewEntities,  int batchSize) {



        List<List<ViewEntity>> batches = BatchUtils.chunk(viewEntities, batchSize);

        List<Future<List<FindAllViewBatchProcResult>>> futures = new ArrayList<>();




        for(List<ViewEntity> batch : batches) {

            Future<List<FindAllViewBatchProcResult>> future =
                    dbBoundedExecutor.submit(() -> executeFindAllViewBatchProc(batch));

            futures.add(future);

        }

        return dbBoundedExecutor.join(futures);
    }

    private List<FindAllViewBatchProcResult> executeFindAllViewBatchProc(List<ViewEntity> batch)  {



        String placeholders = batch.stream()
                .map(k -> "(?, ?)")
                .collect(Collectors.joining(","));

        String sql = """
        SELECT *
        FROM view
        WHERE (board_pk, board_type) IN (%s)
        """.formatted(placeholders);



        return jdbcTemplate.query(
                sql,  ps -> {
                    int i = 1; // PS index는 1부터 시작
                    for (ViewEntity entity : batch) {
                        ps.setLong(i++, entity.getBoardPk());
                        ps.setString(i++, entity.getBoardType().name());
                    }
                },
                (rs, rowNum) ->

            new FindAllViewBatchProcResult(
                    BoardType.valueOf(rs.getString("board_type")),
                    rs.getLong("board_pk"),
                    rs.getLong("view_count")

            )
        );
    }


    private Runnable executeUpdateViewBatchProc(String sql, List<ViewEntity> batch) {
        log.info("executing batch: {}", sql);
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ViewEntity viewEntity = batch.get(i);
                ps.setLong(1, viewEntity.getViewCount());
                ps.setLong(2, viewEntity.getBoardPk());
                ps.setString(3, viewEntity.getBoardType().name());
                log.info("viewCount: {} {} {}", viewEntity.getViewCount(), viewEntity.getBoardPk(), viewEntity.getBoardType());
            }

            @Override
            public int getBatchSize() {
                return batch.size();
            }
        });
        return null;
    }
}
