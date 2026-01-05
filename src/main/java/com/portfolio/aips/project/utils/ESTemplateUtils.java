package com.portfolio.aips.project.utils;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class ESTemplateUtils {

    private static final Logger log = LoggerFactory.getLogger(ESTemplateUtils.class);

    
    @Deprecated //계속된 io로 인해 Bean 방식으로 교체
    public static String loadJson(String resourcePath) throws URISyntaxException, IOException {
        return Files.readString(
                Paths.get(Objects.requireNonNull(ESTemplateUtils.class.getClassLoader()
                        .getResource(resourcePath)).toURI())
        );
    }



    public static ESJsonTemplateRequestBuilder responseBuilder(ElasticsearchClient client) {
        return new ESJsonTemplateRequestBuilder(client);
    }

    public static void createIndex(ElasticsearchClient client, String indexName, String jsonPath) {
        try {
            boolean exists = client.indices().exists(e -> e.index(indexName)).value();

            if (exists) {
                log.warn("인덱스 '{}'가 존재하지만 매핑이 다를 수 있어 삭제 후 재생성합니다.", indexName);
                client.indices().delete(d -> d.index(indexName));
                log.info("인덱스 '{}' 삭제 완료", indexName);
            }
                ClassPathResource resource = new ClassPathResource(jsonPath);
                String json = Files.readString(resource.getFile().toPath());

                CreateIndexResponse response = client.indices().create(c -> c
                        .index(indexName)
                        .withJson(new StringReader(json))
                );

                if (response.acknowledged()) {
                    log.info("인덱스 '{}'가 성공적으로 생성되었습니다!", indexName);
                } else {
                    log.error("인덱스 '{}' 생성에 실패했습니다.", indexName);
                }


        } catch (Exception e) {
            log.error("인덱스 '{}' 생성 중 오류가 발생했습니다.", indexName, e);
        }
    }



}
