package com.portfolio.aips.project.url_service.common.repo;

import com.portfolio.aips.project.url_service.common.entity.URLStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface URLStatusRepository extends JpaRepository<URLStatusEntity,Long> {
        boolean existsByIsCreatedAndUrlLink(Boolean isCreated, String urlLink);
        Optional<URLStatusEntity> findByIsCreatedAndUrlLink(Boolean isCreated, String urlLink);
}
