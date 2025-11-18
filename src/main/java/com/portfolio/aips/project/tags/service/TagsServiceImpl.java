package com.portfolio.aips.project.tags.service;

import com.portfolio.aips.project.tags.dto.CreateTagsCommand;
import com.portfolio.aips.project.tags.entity.BoardTagsEntity;
import com.portfolio.aips.project.tags.entity.TagsEntity;
import com.portfolio.aips.project.tags.repo.BoardTagsRepository;
import com.portfolio.aips.project.tags.repo.TagsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TagsServiceImpl implements TagsService {
    private final TagsRepository tagsRepository;
    private final BoardTagsRepository boardTagsRepository;

    @Override
    @Transactional
    public void createTagProc(CreateTagsCommand createTagsCommand) {
        List<String> tagNames = createTagsCommand.tagNames();
        Long boardId = createTagsCommand.boardId();
        String boardType = createTagsCommand.boardType();

        List<TagsEntity> existingTags = tagsRepository.findAllByNameIn(tagNames);
        Map<String, TagsEntity> existingTagMap = existingTags.stream()
                .collect(Collectors.toMap(TagsEntity::getName, Function.identity()));


        List<TagsEntity> newTags = tagNames.stream()
                .filter(tagName -> !existingTagMap.containsKey(tagName))
                .map(TagsEntity::new)
                .toList();

        List<TagsEntity> savedTags = tagsRepository.saveAll(newTags);


        Map<String, TagsEntity> allTagsMap = Stream.concat(existingTags.stream(), savedTags.stream())
                .collect(Collectors.toMap(TagsEntity::getName, Function.identity()));


        List<BoardTagsEntity> boardTags = tagNames.stream()
                .map(tagName -> BoardTagsEntity.builder()
                        .boardId(boardId)
                        .boardType(boardType)
                        .tags(allTagsMap.get(tagName))
                        .build())
                .toList();


        boardTagsRepository.saveAll(boardTags);


    }
}
