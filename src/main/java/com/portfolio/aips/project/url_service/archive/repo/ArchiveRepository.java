package com.portfolio.aips.project.url_service.archive.repo;

import com.portfolio.aips.project.url_service.archive.entity.ArchiveEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArchiveRepository extends JpaRepository<ArchiveEntity, Long> {
        boolean existsBySiteSlug(String siteSlug);
        boolean existsByPk(Long pk);

}
