package com.portfolio.aips.project.tags.service;

import com.portfolio.aips.project.tags.dto.CreateTagsCommand;
import com.portfolio.aips.project.tags.entity.BoardTagsEntity;
import com.portfolio.aips.project.tags.entity.TagsEntity;
import com.portfolio.aips.project.tags.repo.BoardTagsRepository;
import com.portfolio.aips.project.tags.repo.TagsRepository;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional

class TagsServiceTest {

    @Autowired
    private TagsRepository tagsRepository;

    @Autowired
    private BoardTagsRepository boardTagsRepository;

    @Autowired
    private TagsService tagService; // 위에서 작성한 서비스

    @Autowired
    private EntityManagerFactory emf;

    @Test
    void createTags_savesNewTagsAndBoardTags() {

        SessionFactory sessionFactory = emf.unwrap(SessionFactory.class);
        sessionFactory.getStatistics().setStatisticsEnabled(true);

        // given
        List<String> tagNames = List.of("AI", "ChatGPT", "LLM");
        Long boardId = 1L;
        String boardType = "BOARD";

        CreateTagsCommand command = new CreateTagsCommand(boardId, boardType, tagNames);

        // when
        tagService.createTagProc(command);

        // then
        // 1️⃣ TagsEntity가 모두 생성되었는지
        List<TagsEntity> tagsInDb = tagsRepository.findAllByNameIn(tagNames);

        assertEquals(3, tagsInDb.size());
        assertTrue(tagsInDb.stream().anyMatch(t -> t.getName().equals("AI")));
        assertTrue(tagsInDb.stream().anyMatch(t -> t.getName().equals("ChatGPT")));
        assertTrue(tagsInDb.stream().anyMatch(t -> t.getName().equals("LLM")));

        // 2️⃣ BoardTagsEntity가 올바르게 저장되었는지
        List<BoardTagsEntity> boardTags = boardTagsRepository.findAll();
        assertEquals(3, boardTags.size());

        for (BoardTagsEntity bt : boardTags) {
            assertEquals(boardId, bt.getBoardId());
            assertEquals(boardType, bt.getBoardType());
            assertTrue(tagNames.contains(bt.getTags().getName()));
        }

        System.out.println("Insert Count: " + sessionFactory.getStatistics().getEntityInsertCount());
        System.out.println("Prepare Statement Count: " + sessionFactory.getStatistics().getPrepareStatementCount());

    }
}