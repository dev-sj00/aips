package com.portfolio.aips.project.interaction.view.repo;

import com.portfolio.aips.project.interaction.view.entity.ViewEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ViewBatchRepositoryImpl implements ViewBatchRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ExecutorService vtExecutor;
    private final Semaphore dbSemaphore;
    private final EntityManager em;


    @Override
    public void updateViewBatchProc(List<ViewEntity> viewEntities,  int batchSize) {
        String sql = "INSERT INTO views (board_pk, board_type, view_count) VALUES (?, ?, ?) " +
                "ON CONFLICT (board_pk, board_type) DO UPDATE SET view_count = EXCLUDED.view_count";


        int total = viewEntities.size();

        for(int start = 0; start < total; start+=batchSize) {
            int end = Math.min(start+batchSize, total);

            List<ViewEntity> subList =
                    new ArrayList<>(viewEntities.subList(start, end));

            vtExecutor.submit(() -> {
                try {

                    if (!dbSemaphore.tryAcquire(5, TimeUnit.SECONDS)) {
                        log.warn("DB 세마포어 타임아웃, 배치 스킵");
                        return;
                    }

                    dbSemaphore.acquire();   // 남아 있는 값이 없을 시 block
                    executeBatch(sql, subList);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("배치 인터럽트 에러", e);
                } finally {
                    dbSemaphore.release(); // 세마포어 반환
                }
            });
        }

        em.clear(); // 배치 작업후 초기화


    }

    private void executeBatch(String sql, List<ViewEntity> batch) {
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ViewEntity viewEntity = batch.get(i);
                ps.setLong(1, viewEntity.getBoardPk());
                ps.setString(2, viewEntity.getBoardType().toString());
                ps.setLong(3, viewEntity.getViewCount());
            }

            @Override
            public int getBatchSize() {
                return batch.size();
            }
        });
    }
}
