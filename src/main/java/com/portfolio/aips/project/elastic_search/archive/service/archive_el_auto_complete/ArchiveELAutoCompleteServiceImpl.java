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

    @Override
    public List<String> autocomplete(String keyword) throws URISyntaxException, IOException {
        String jsonTemplate = ESTemplateUtils.loadJson("elastic/queries/archive_autocomplete.json");
        String jsonQuery = String.format(jsonTemplate, keyword, keyword, keyword);

        Response response = ESTemplateUtils.responseBuilder(client)
                .body(jsonQuery)
                .url("/archive/_search")
                .method("POST")
                .execute();

        Map<String, Object> root = objectMapper.readValue(
                response.getEntity().getContent(),
                new TypeReference<>() {
                }
        );

        Object innerHitsObj = ((Map<?, ?>) root.get("hits")).get("hits");

        
        //타입 안전하게 변환 convertValue + TypeReference
        List<Map<String, Object>> hits = objectMapper.convertValue(
                innerHitsObj,
                new TypeReference<>() {
                }
        );

        log.info("hits {}", hits);

        Set<String> results = new LinkedHashSet<>();

        for (Map<String, Object> hit : hits) {

            // "_source" 를 완전 타입 안전하게 변환
            Map<String, Object> source = objectMapper.convertValue(
                    hit.get("_source"),
                    new TypeReference<>() {
                    }
            );

            //title
            Optional.ofNullable(source.get("title"))
                    .map(String::valueOf)
                    .filter(title -> title.contains(keyword)) // 검색어 포함 여부 확인
                    .ifPresent(results::add);

            // tags : Object → List<String> 안전 변환
            Optional.ofNullable(source.get("tags"))
                    .map(value -> objectMapper.convertValue(value, new TypeReference<List<String>>() {}))
                    .orElseGet(List::of)
                    .stream()
                    .filter(tag -> tag.contains(keyword))
                    .forEach(results::add);  // 포함된 값만 results에 추가

            Optional.ofNullable(source.get("description"))
                    .map(String::valueOf)
                    .filter(title -> title.contains(keyword)) // 검색어 포함 여부 확인
                    .ifPresent(results::add);

            if (results.size() >= 9) break;
        }


        return results.stream().limit(9).toList();


    }
}
