package com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.repo;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.command.GetTrendingKeywordsCommand;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.enums.SearchDateRange;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.service.TrendingScoreCalculator.command.CalculateScoreCommand;
import com.portfolio.aips.project.utils.ESTemplateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ArchiveELTrendingSearchLogElasticRepositoryImpl implements ArchiveELTrendingSearchLogElasticRepository {

    private final String archiveGetTrendingKeywordsTemplate;
    private final ElasticsearchClient client;

    @Override
    public HashMap<String, CalculateScoreCommand> findBySearchDateRangeAndCommand(SearchDateRange range, GetTrendingKeywordsCommand command) throws IOException, URISyntaxException {
        String jsonQuery =  getJsonQueryWithFormat(archiveGetTrendingKeywordsTemplate, range, command);


        log.info("jsonQuery: {}", jsonQuery);
        Response response = ESTemplateUtils.responseBuilder(client)
                .body(jsonQuery)
                .url("/archive_search_log/_search")
                .method("POST")
                .execute();

        String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

        log.info("aggregations {}", responseBody);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(responseBody);



        return  getCalculateCommandMap(root);

    }

    private HashMap<String, CalculateScoreCommand> getCalculateCommandMap(JsonNode root) {
        JsonNode currentBuckets = root.path("aggregations")
                .path("periods")
                .path("buckets")
                .path("current")
                .path("trending_keywords")
                .path("buckets");

        JsonNode prevBuckets = root.path("aggregations")
                .path("periods")
                .path("buckets")
                .path("prev")
                .path("trending_keywords")
                .path("buckets");


        //구현 중


        HashMap<String, CalculateScoreCommand> resultMap = new HashMap<>();


        currentBuckets.forEach(b ->
                resultMap.put(b.path("key").asText(),
                        new CalculateScoreCommand(0, b.path("doc_count").asLong()))
        );


        prevBuckets.forEach(b-> {
            String key = b.path("key").asText();
            long docCount = b.path("doc_count").asLong();

            resultMap.computeIfPresent(key,
                    (k, v) -> new CalculateScoreCommand(docCount, v.currentDocCount()));
        });

        return resultMap;
    }


    private String getJsonQueryWithFormat(String jsonTemplate, SearchDateRange range, GetTrendingKeywordsCommand command) throws URISyntaxException, IOException {
        String currentGte = range.getCurrentGte();
        String currentLte = range.getCurrentLte();
        String prevGte = range.getPrevGte();
        String prevLte = range.getPrevLte();
        int reqSize = command.reqSize();


        return String.format(
                jsonTemplate,
                currentGte,
                currentLte,
                prevGte,
                prevLte,
                reqSize
        );
    }

}
