package com.portfolio.aips.project.elastic_search.archive.service.archive_el;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.portfolio.aips.project.elastic_search.archive.document.ArchiveDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArchiveELServiceImpl implements ArchiveELService{
    private final ElasticsearchClient  client;

    @Override
    public void save(ArchiveDocument doc) {
        try{
            IndexResponse response = client.index(i -> i
                    .index("archive")
                    .id(doc.getPk())
                    .document(doc)
            );

            client.indices().refresh(r -> r.index("archive"));

            log.info("문서 색인 완료, 결과 : {}", response.result());
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }
}
