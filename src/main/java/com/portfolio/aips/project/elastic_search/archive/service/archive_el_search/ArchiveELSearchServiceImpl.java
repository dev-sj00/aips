package com.portfolio.aips.project.elastic_search.archive.service.archive_el_search;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.portfolio.aips.project.elastic_search.archive.dto.ArchiveDocument;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_search.command.SearchByConditionCommand;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_search.command.SearchByConditionCreateAtCommand;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_search.enums.SearchDateRange;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_search.enums.SearchSortType;
import jakarta.json.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArchiveELSearchServiceImpl implements ArchiveELSearchService{

    private final ElasticsearchClient client;

    @Override
    public List<ArchiveDocument> searchAll(String keyword) throws IOException {
        String template = new ClassPathResource("elastic/queries/archive_search.json")
                .getContentAsString(StandardCharsets.UTF_8);

        String json = String.format(template, keyword, keyword);


        return searchResultMapper(json);
    }

    private List<ArchiveDocument> searchResultMapper(String json) throws IOException {
        SearchResponse<ArchiveDocument> response = client.search(
                s -> s.index("archive")
                        .withJson(new StringReader(json)),
                ArchiveDocument.class
        );

        log.info(response.toString());


        List<ArchiveDocument> results = new ArrayList<>();

        for (Hit<ArchiveDocument> hit : response.hits().hits()) {
            ArchiveDocument doc = hit.source();
            Map<String, List<String>> highlight = hit.highlight();



            if (highlight != null && doc != null) {
                if (highlight.containsKey("title")) {
                    String titleHl = String.join(" ... ", highlight.get("title"));

                    doc.setTitle(titleHl);

                }
                if (highlight.containsKey("description")) {
                    String descHl = String.join(" ... ", highlight.get("description"));

                    doc.setDescription(descHl);
                }
            }
            results.add(doc);
        }

        return results;
    }


    @Override
    public List<ArchiveDocument> searchByCondition(SearchByConditionCommand command) throws IOException {
        //기존 searchAll json Template
        Map<String, Object> root = new HashMap<>();
        Map<String, Object> query = new HashMap<>();
        Map<String, Object> bool = new HashMap<>();

        bool.put("should", buildShould(command.keyword()));
        bool.put("minimum_should_match", 1);
        List<Object> filter = buildFilter(command.topic(), command.llmType(), command.dateRange());

        if(!filter.isEmpty()){
            bool.put("filter", filter);
        }

        query.put("bool", bool);
        root.put("query", query);

        root.put("highlight", buildHighlight());

        //condition 템플릿 코드
        root.put("from", command.pageFrom());
        root.put("size", command.pageSize());


        List<Object> sort =  buildSort(command.sortType());

        if(!sort.isEmpty()){
            root.put("sort", sort);
        }



        String json = new GsonBuilder().setPrettyPrinting().create().toJson(root);

        log.info("json = {}", json);
        return searchResultMapper(json);
    }

    private List<Object> buildSort(SearchSortType sortType)
    {

        List<Object> sort = new ArrayList<>();

        if(sortType == SearchSortType.Latest) {
            Map<String, Object> createdAt = new HashMap<>();
            createdAt.put("order", "desc");
            sort.add(Map.of("createdDateTime", createdAt));

        }

        return sort;

    }

    private Map<String, Object> buildHighlight(){
        Map<String, Object> highlight = new HashMap<>();
        highlight.put("fields", Map.of("title", new HashMap<>(), "description", new HashMap<>()));
        highlight.put("pre_tags", List.of("<em>"));
        highlight.put("post_tags", List.of("</em>"));

        return highlight;
    }

    private List<Object> buildShould(String keyword)
    {
        List<Object> should = new ArrayList<>();
        Map<String, Object> multiMatch = new HashMap<>();
        multiMatch.put("query", keyword);
        multiMatch.put("fields", List.of("title^3", "description", "tags^2"));
        multiMatch.put("type", "best_fields");
        multiMatch.put("fuzziness", "AUTO");
        multiMatch.put("prefix_length", 1);
        multiMatch.put("operator", "or");
        should.add(Map.of("multi_match", multiMatch));
        return should;

    }

    private List<Object> buildFilter(String topic, String llmType, SearchDateRange dateRange) {
        List<Object> filter = new ArrayList<>();

        if(dateRange != null) {
            Map<String, Object> createdAt = new HashMap<>(); //날짜 별로 검색
            createdAt.put("gte", dateRange.getGte());
            createdAt.put("lte", dateRange.getLte());

            Map<String, Object> range = new HashMap<>(); //날짜 별로 검색
            range.put("createdDateTime", createdAt);

            filter.add(Map.of("range", range));
        }

        if(topic != null) {
            Map<String, Object> term = new HashMap<>(); //특정 문서만 검색
            term.put("topic", topic);


            filter.add(Map.of("term", term));
        }

        if(llmType != null) {
            Map<String, Object> term = new HashMap<>();
            term.put("llmType", llmType);
            filter.add(Map.of("term", term));
        }

        return filter;
    }

}
