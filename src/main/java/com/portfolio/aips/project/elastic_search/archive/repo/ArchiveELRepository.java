package com.portfolio.aips.project.elastic_search.archive.repo;

import com.portfolio.aips.project.elastic_search.archive.document.ArchiveDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@EnableElasticsearchRepositories
public interface ArchiveELRepository extends ElasticsearchRepository<ArchiveDocument, String> {

    Iterable<ArchiveDocument> findByTitle(String title);
}
