package com.portfolio.aips.project.archive.repo;

import com.portfolio.aips.project.archive.entity.ArchiveEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArchiveRepository extends JpaRepository<ArchiveEntity, Long> {
        Optional<ArchiveEntity> findByArchiveLink(String archiveLink);
        boolean existsBySiteSlug(String siteSlug);

}
