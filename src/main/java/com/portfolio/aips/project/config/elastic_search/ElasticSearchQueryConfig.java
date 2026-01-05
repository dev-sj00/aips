package com.portfolio.aips.project.config.elastic_search;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class ElasticSearchQueryConfig {

    @Bean(name = "archiveSearchQueryTemplate")
    public String archiveSearchQueryTemplate() throws IOException {
        return new ClassPathResource("elastic/queries/archive_search.json")
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Bean(name = "archiveAutocompleteQueryTemplate")
    public String archiveAutocompleteQueryTemplate() throws IOException {
        return new ClassPathResource("elastic/queries/archive_autocomplete.json")
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Bean(name = "archiveGetTrendingKeywordsTemplate")
    public String archiveGetTrendingKeywordsTemplate() throws IOException {
        return new ClassPathResource("elastic/queries/archive_get_trending_keywords.json")
                .getContentAsString(StandardCharsets.UTF_8);
    }

}
