package com.portfolio.aips.project.tags.repo;

import com.portfolio.aips.project.tags.entity.BoardTagsEntity;
import com.portfolio.aips.project.tags.entity.TagsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardTagsRepository extends JpaRepository<BoardTagsEntity, Long> {
}
