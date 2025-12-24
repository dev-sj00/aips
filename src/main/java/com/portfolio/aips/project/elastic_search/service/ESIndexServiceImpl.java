package com.portfolio.aips.project.elastic_search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ESIndexServiceImpl implements ESIndexService {

    private final ElasticsearchClient client;

    public <T> void save(String indexName, String id, T document) {
        try {
            IndexResponse response = client.index(i -> i
                    .index(indexName)
                    .id(id)
                    .document(document)
            );

            client.indices().refresh(r -> r.index(indexName));

            log.info("문서 색인 완료 [index={}, id={}, result={}]",
                    indexName, id, response.result());

        } catch (Exception e) {
            log.error("문서 색인 실패 [index={}, id={}]", indexName, id, e);
        }
    }
}
