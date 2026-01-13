package com.portfolio.aips.project.elastic_search.view.view_el_bulk;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import com.portfolio.aips.project.elastic_search.view.view_el_bulk.command.UpdateViewCountProcCommand;
import com.portfolio.aips.project.elastic_search.view.view_el_bulk.enums.IndexType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

@Service
@RequiredArgsConstructor
@Slf4j
public class ViewELBulkServiceImpl implements ViewELBulkService {
    private final ElasticsearchClient esClient;
    private final ExecutorService vtExecutor;
    private final Semaphore esSemaphore;

    private static final int BLOCKING_QUEUE_CAPACITY = 10000;
    private static final int BULK_QUEUE_CAPACITY = 500;

    BlockingQueue<BulkOperation> queue = new LinkedBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);

    @Override
    public void updateViewCountProc(List<UpdateViewCountProcCommand> commands) throws IOException {



        for(UpdateViewCountProcCommand command : commands) {
            BulkOperation op = getUpdateViewCountBulkOperation(command);
            boolean offered = queue.offer(op);
            if (!offered) {
                log.warn("ES Bulk 큐가 다 찼음, 다음 스케줄러에서 처리: {}", command);

            }
        }

        while(!queue.isEmpty()) {
            List<BulkOperation> bulkOperations = new ArrayList<>(BULK_QUEUE_CAPACITY);

            queue.drainTo(bulkOperations, BULK_QUEUE_CAPACITY);

            vtExecutor.submit(() -> {
                try {
                    executeUpdateViewCountProc(bulkOperations);
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            });

        }


    }

    private void executeUpdateViewCountProc(List<BulkOperation> bulkOperations) throws IOException {
        try {

            esSemaphore.acquire();
            BulkResponse response = esClient.bulk(b-> b.operations(bulkOperations));

            if (response.errors()) {
                response.items().forEach(item -> {
                    if (item.error() != null) {
                        log.error(
                                "ES bulk update failed. index={}, id={}, reason={}",
                                item.index(),
                                item.id(),
                                item.error().reason()
                        );
                    }
                });


            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Bulk execution interrupted", e);
        } finally {
            esSemaphore.release();
        }
    }

    private BulkOperation getUpdateViewCountBulkOperation(UpdateViewCountProcCommand command) {

        return BulkOperation.of(op -> op
                .update(u -> u
                        .index(IndexType.from(command.boardType()))
                        .id(command.boardPk().toString())
                        .action(a -> a
                                .doc(Map.of("viewCount", command.viewCount()))
                                .docAsUpsert(false) // doc 삭제 시 insert 안 함
                        )
                )
        );
    }


}
