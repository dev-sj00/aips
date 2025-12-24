package com.portfolio.aips.project.elastic_search.archive.service.archive_el;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.portfolio.aips.project.elastic_search.archive.dto.ArchiveDocument;
import com.portfolio.aips.project.elastic_search.archive.dto.ArchiveSearchLogDocument;
import com.portfolio.aips.project.elastic_search.service.ESIndexService;
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
    private final ESIndexService esIndexService;

    @Override
    public void save(ArchiveDocument doc) {
        esIndexService.save("archive", doc.getPk(), doc);

    }

    @Override
    public void save(ArchiveSearchLogDocument doc)
    {
        esIndexService.save("archive_search_log", doc.pk(), doc);
    }


}
