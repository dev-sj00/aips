package com.portfolio.aips.project.elastic_search.service;

public interface ESIndexService {
    <T> void save(String indexName, String id, T document);
}
