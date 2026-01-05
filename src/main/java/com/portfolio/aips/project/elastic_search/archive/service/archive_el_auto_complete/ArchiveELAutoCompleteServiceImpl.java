package com.portfolio.aips.project.elastic_search.archive.service.archive_el_auto_complete;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.aips.project.utils.ESTemplateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArchiveELAutoCompleteServiceImpl implements ArchiveELAutoCompleteService {

    private final ElasticsearchClient client;
    private final ObjectMapper objectMapper;
    private final String archiveAutocompleteQueryTemplate;

    @Override
    public List<String> autocomplete(String keyword)  {
        try {

            String jsonQuery = String.format(archiveAutocompleteQueryTemplate, keyword);

            Response response = ESTemplateUtils.responseBuilder(client)
                    .body(jsonQuery)
                    .url("/archive/_search")
                    .method("POST")
                    .execute();

            Map<String, Object> root = objectMapper.readValue(
                    response.getEntity().getContent(),
                    new TypeReference<>() {}
            );

            Object innerHitsObj = ((Map<?, ?>) root.get("hits")).get("hits");
            List<Map<String, Object>> hits = objectMapper.convertValue(
                    innerHitsObj,
                    new TypeReference<>() {}
            );

            log.info("hits: {}", hits.toString());

            return collectAutocompleteResults(hits, keyword);

        } catch (Exception e) {
            log.error("자동완성 조회 실패: keyword={}", keyword, e);
            return List.of();
        }


    }

    private List<String> collectAutocompleteResults(List<Map<String, Object>> hits, String keyword) {
        Set<String> results = new LinkedHashSet<>();

        for (Map<String, Object> hit : hits) {
            if (results.size() >= 9) break;

            Map<String, Object> source = objectMapper.convertValue(
                    hit.get("_source"),
                    new TypeReference<>() {}
            );


            // title
            Optional.ofNullable(source.get("title"))
                    .map(String::valueOf)
                    .filter(value -> value.contains(keyword))
                    .ifPresent(results::add);

            // tags
            Optional.ofNullable(source.get("tags"))
                    .map(value -> objectMapper.convertValue(value, new TypeReference<List<String>>() {}))
                    .orElseGet(List::of)
                    .stream()
                    .filter(tag -> tag.contains(keyword))
                    .forEach(results::add);

            // description
            Optional.ofNullable(source.get("description"))
                    .map(String::valueOf)
                    .filter(value -> value.contains(keyword))
                    .ifPresent(results::add);
        }

        return results.stream().limit(9).toList();
    }

}
