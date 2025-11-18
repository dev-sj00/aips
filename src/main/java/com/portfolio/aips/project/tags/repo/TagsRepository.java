package com.portfolio.aips.project.tags.repo;

import com.portfolio.aips.project.tags.dto.CreateTagsCommand;
import com.portfolio.aips.project.tags.entity.BoardTagsEntity;
import com.portfolio.aips.project.tags.entity.TagsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TagsRepository extends JpaRepository<TagsEntity, Long> {
    List<TagsEntity> findAllByNameIn(List<String> names);
}
