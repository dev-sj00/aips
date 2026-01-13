package com.portfolio.aips.project.interaction.rating.service.rating.service;

import com.portfolio.aips.project.interaction.enums.BoardType;
import com.portfolio.aips.project.interaction.rating.service.rating.command.SaveCommand;
import com.portfolio.aips.project.url_service.archive.entity.ArchiveEntity;
import com.portfolio.aips.project.url_service.archive.repo.ArchiveRepository;
import com.portfolio.aips.project.url_service.common.entity.URLStatusEntity;
import com.portfolio.aips.project.url_service.common.service.url_generator.enums.URLGeneratorType;
import com.portfolio.aips.project.utils.enums.LLMType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class RatingServiceTest {
    @Autowired
    private RatingService ratingService;

    @Autowired
    private EntityManager em;

    @Autowired
    private ArchiveRepository archiveRepository;


    private ArchiveEntity getDummyArchiveEntity(URLStatusEntity urlStatusEntity) {
        ArchiveEntity archiveEntity = new ArchiveEntity();
        archiveEntity.setTitle("AI 글 요약 서비스");
        archiveEntity.setSiteSlug("ai-summary");
        archiveEntity.setDescription("긴 글을 자동으로 요약해주는 AI 서비스입니다.");
        archiveEntity.setUserPk(2L);
        archiveEntity.setLlmType(LLMType.CHATGPT);
        archiveEntity.setUrlStatusEntity(urlStatusEntity);
        return archiveEntity;
    }

    private URLStatusEntity getDummyUrlStatus() {
        return URLStatusEntity.builder()
                .urlLink("https://chat.openai.com")
                .urlType(URLGeneratorType.Archive)
                .build();
    }


    @Test
    @Transactional
    void save_existingEntity_thenUpdate() {

        // given
        ArchiveEntity archiveEntity = getDummyArchiveEntity(getDummyUrlStatus());
        archiveRepository.save(archiveEntity);
        em.flush();
        em.clear();

        ratingService.save(  new SaveCommand(archiveEntity.getPk(),
                BoardType.Archive,
        3,
        4,
        5,
        2L));

        em.flush();
        em.clear();

        SaveCommand command = new SaveCommand(
                archiveEntity.getPk(), BoardType.Archive, 5, 4, 3, 1L
        );

        // when
        ratingService.save(command);
        em.flush();
        em.clear();

        // then
        /*RatingEntity savedRating = em.createQuery(
                        "select r from RatingEntity r where r.boardPk = :boardPk " +
                                "and r.boardType = :boardType and r.raterUserPk = :raterUserPk",
                        RatingEntity.class
                )
                .setParameter("boardPk", archiveEntity.getPk())
                .setParameter("boardType", BoardType.Archive)
                .setParameter("raterUserPk", 2L)
                .getSingleResult();

        assertNotNull(savedRating);*/


        SaveCommand updateCommand = new SaveCommand(
                archiveEntity.getPk(), BoardType.Archive, 5, 2, 3, 1L
        );

        ratingService.save(updateCommand);
        em.flush();
        em.clear();


    }
}