package com.portfolio.aips.project.elastic_search.archive.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.portfolio.aips.project.utils.ESTemplateUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ArchiveIndexInitializer {

    private final ElasticsearchClient client;
    @Value("${app.env.dev:true}")
    private boolean isDev;



    @PostConstruct
    public void init() {
        if(isDev) {
            try {
                ESTemplateUtils.createIndex(client, "archive", "elastic/index/archive_index.json");
                log.info("archive index 생성");
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

    }

}
