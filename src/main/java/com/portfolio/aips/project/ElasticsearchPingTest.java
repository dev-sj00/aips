package com.portfolio.aips.project;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;

@Component
public class ElasticsearchPingTest {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @PostConstruct
    public void ping() {
        boolean isAvailable = elasticsearchOperations.indexOps(TestDocument.class).exists();
        System.out.println("Elasticsearch 연결 가능 여부: " + isAvailable);
    }

}