package com.portfolio.aips.project.elastic_search.view.view_el_bulk;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import com.portfolio.aips.project.elastic_search.view.view_el_bulk.command.UpdateViewCountProcCommand;
import com.portfolio.aips.project.elastic_search.view.view_el_bulk.enums.IndexType;
import com.portfolio.aips.project.utils.virtual_thread_utils.VirtualThreadBoundedExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ViewELBulkServiceImpl implements ViewELBulkService {
    private final ElasticsearchClient esClient;
    private final ExecutorService vtExecutor;
    private final Semaphore esSemaphore;

    private static final int BLOCKING_QUEUE_CAPACITY = 10000;

    BlockingQueue<BulkOperation> queue = new LinkedBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);


    @Override
    public void updateViewCountProc(List<UpdateViewCountProcCommand> commands){



        for(UpdateViewCountProcCommand command : commands) {
            BulkOperation op = getUpdateViewCountBulkOperation(command);
            boolean offered = queue.offer(op);
            log.info("commands");
            if (!offered) {
                log.warn("BulkOperation queue full. drop op. command={}", command);
            }
        }

        VirtualThreadBoundedExecutor
                .builder()
                .semaphore(esSemaphore)
                .executor(vtExecutor)
                .timeout(5, TimeUnit.SECONDS)
                .build()
                .executeBatched(queue, 500,  this::executeUpdateViewCountProc);

        log.info("ViewELBulkServiceImpl.updateViewCountProc");



    }

    private void executeUpdateViewCountProc(List<BulkOperation> bulkOperations)  {
        try {

            BulkResponse response = esClient.bulk(b-> b.operations(bulkOperations));
            log.info("bulk operation response={}", response);
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
            log.info("executeUpdateViewCountProc");
        } catch (IOException e) {
            throw new RuntimeException(e);
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
