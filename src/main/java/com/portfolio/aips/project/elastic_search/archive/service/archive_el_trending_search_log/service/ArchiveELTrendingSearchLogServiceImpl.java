package com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.service;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.command.GetTrendingKeywordsCommand;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.result.GetTrendingKeywordsResult;
import com.portfolio.aips.project.utils.ESTemplateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArchiveELTrendingSearchLogServiceImpl implements ArchiveELTrendingSearchLogService {

    private final ElasticsearchClient client;
    private final ObjectMapper objectMapper;

    @Override
    public List<GetTrendingKeywordsResult> getTrendingKeywords(GetTrendingKeywordsCommand command) throws URISyntaxException, IOException {
        String jsonTemplate = ESTemplateUtils.loadJson("elastic/queries/archive_get_trending_keywords.json");
        String jsonQuery = String.format(jsonTemplate, command.searchDateRange().getGte(), command.searchDateRange().getLte(), command.size());


        log.info("jsonQuery: {}", jsonQuery);
        Response response = ESTemplateUtils.responseBuilder(client)
                .body(jsonQuery)
                .url("/archive_search_log/_search")
                .method("POST")
                .execute();

        String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

        log.info("aggregations {}", responseBody);

        return List.of();
    }
}
