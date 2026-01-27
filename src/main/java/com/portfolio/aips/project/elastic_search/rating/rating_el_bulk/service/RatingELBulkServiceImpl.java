package com.portfolio.aips.project.elastic_search.rating.rating_el_bulk.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.json.JsonData;
import com.portfolio.aips.project.elastic_search.rating.rating_el_bulk.service.command.UpdateRatingAndPopularityScoreBulkProcCommand;
import com.portfolio.aips.project.elastic_search.view.view_el_bulk.command.UpdateViewCountProcCommand;
import com.portfolio.aips.project.elastic_search.view.view_el_bulk.enums.IndexType;
import com.portfolio.aips.project.interaction.rating.repo.result.PopularityScoreElementsResult;
import com.portfolio.aips.project.interaction.rating.service.popularity_score_calculate.result.CalculatePopularityScoreResult;
import com.portfolio.aips.project.utils.ESTemplateUtils;
import com.portfolio.aips.project.utils.virtual_thread_utils.BoundedExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingELBulkServiceImpl implements RatingELBulkService {

    private final ElasticsearchClient esClient;
    private final BoundedExecutor esBoundedExecutor;

    private static final int BLOCKING_QUEUE_CAPACITY = 20000;

    BlockingQueue<BulkOperation> queue = new LinkedBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);


    @Override
    public void updateRatingAndPopularityScoreBulkProc(List<UpdateRatingAndPopularityScoreBulkProcCommand> commands) {


        for (UpdateRatingAndPopularityScoreBulkProcCommand command : commands) {

            BulkOperation op = getUpdateViewCountBulkOperation(command);

            boolean offered = queue.offer(op);

            if(!offered){
                log.warn("BulkOperation queue full. drop op. command={}", command);
            }

            esBoundedExecutor
                    .executeBatched(queue, 500,  this::executeUpdateRatingAndPopularityScoreBulkProc);


        }

    }


    private void executeUpdateRatingAndPopularityScoreBulkProc(List<BulkOperation> op) {
        ESTemplateUtils.executeBulk(esClient, op);
    }



    private BulkOperation getUpdateViewCountBulkOperation(UpdateRatingAndPopularityScoreBulkProcCommand command) {


        log.info("Getting view count for command={}", command.toString());

        Map<String, Object> updates = new HashMap<>();
        updates.put("usefulnessAvgScore", command.usefulnessAvgScore());
        updates.put("reliabilityAvgScore", command.reliabilityAvgScore());
        updates.put("funAvgScore", command.funAvgScore());
        updates.put("ratingCount", command.ratingCount());
        updates.put("popularityScore", command.popularityScore());

// 1. 스크립트 source 생성
        StringBuilder scriptSource = new StringBuilder();
        updates.keySet().forEach(field -> {
            scriptSource.append("ctx._source.").append(field).append(" += params.").append(field).append("; ");
        });

        Map<String, JsonData> jsonParams = new HashMap<>();
        updates.forEach((k, v) -> jsonParams.put(k, JsonData.of(v)));



        return BulkOperation.of(op -> op
                .update(u -> u
                        .index(IndexType.from(command.boardType()))
                        .id(command.boardPk().toString())
                        .action(a -> a
                                .script(s -> s
                                        .inline(b -> b
                                                .source(scriptSource.toString())
                                                .params(jsonParams)
                                        )
                        )
                )
        )
        );
    }
}
