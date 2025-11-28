package com.portfolio.aips.project.elastic_search.archive.service.archive_el;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.portfolio.aips.project.elastic_search.archive.dto.ArchiveDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


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

    @Override
    public void search(String keyword) throws IOException {
        String template = new ClassPathResource("elastic/queries/archive_search.json")
                .getContentAsString(StandardCharsets.UTF_8);

        String json = String.format(template, keyword, keyword);

        SearchResponse<ArchiveDocument> response = client.search(
                s -> s.index("archive")
                        .withJson(new StringReader(json)),
                ArchiveDocument.class
        );

        log.info(response.toString());
        List<ArchiveDocument> documents = response.hits().hits().stream().map(Hit::source).toList();

        for(ArchiveDocument archiveDocument : documents){
            log.info(archiveDocument.toString());
        }


        for (Hit<ArchiveDocument> hit : response.hits().hits()) {
            ArchiveDocument doc = hit.source();
            Map<String, List<String>> highlight = hit.highlight();

            String titleHl = null;
            String descHl = null;

            if (highlight != null) {
                titleHl = String.join(" ... ", highlight.getOrDefault("title", List.of()));
                descHl = String.join(" ... ", highlight.getOrDefault("description", List.of()));
            }

            log.info("title = {}" , doc.getTitle());
            log.info("title highlight = {}", titleHl);
            log.info("description highlight = {}", descHl);


        }

    }
}
