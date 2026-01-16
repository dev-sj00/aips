package com.portfolio.aips.project.interaction.view.repo;

import com.portfolio.aips.project.interaction.enums.BoardType;
import com.portfolio.aips.project.interaction.view.entity.ViewEntity;
import com.portfolio.aips.project.interaction.view.repo.dto.request.FindAllViewBatchProcDTO;
import com.portfolio.aips.project.interaction.view.repo.dto.result.FindAllViewBatchProcResult;
import com.portfolio.aips.project.utils.BatchUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ViewBatchRepositoryImpl implements ViewBatchRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ExecutorService vtExecutor;
    private final Semaphore dbSemaphore;


    @Override
    public void     updateViewBatchProc(List<ViewEntity> viewEntities,  int batchSize) throws InterruptedException {
        String sql = "UPDATE view SET view_count = ? WHERE board_pk = ? AND board_type = ?";



        List<List<ViewEntity>> batches = BatchUtils.chunk(viewEntities, 500);

        for(List<ViewEntity> batch : batches) {
            boolean acquired = dbSemaphore.tryAcquire(5, TimeUnit.SECONDS);

            vtExecutor.submit(() -> {
                try {
                    if (!acquired) {
                        log.warn("DB 세마포어 타임아웃, 배치 스킵");
                        return;
                    }
                    executeUpdateViewBatchProc(sql, batch);
                } finally {
                    if (!acquired) { //세마포어 획득 못했을 시 반환 안함
                        dbSemaphore.release(); // 세마포어 반환
                    }
                }
            });

        }

        /*em.clear();  1차 캐시 1분마다 초기화, elastic 에서 조회하기 때문에 필요 없음 */


    }





    private void executeUpdateViewBatchProc(String sql, List<ViewEntity> batch) {
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
    }
}
