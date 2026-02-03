package com.portfolio.aips.project.visitor.domain.repo;


public interface VisitorCacheRepository {
    void save(Long userPk);
    Long findAll();
}
