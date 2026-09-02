package com.portfolio.aips.project.interaction.rating.service.rating_scheduler;

import com.portfolio.aips.project.elastic_search.archive.dto.ArchiveDocument;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el.ArchiveELService;
import com.portfolio.aips.project.interaction.common.enums.BoardType;
import com.portfolio.aips.project.interaction.rating.service.rating.command.SaveCommand;
import com.portfolio.aips.project.interaction.rating.service.rating.service.RatingService;
import com.portfolio.aips.project.interaction.view.entity.ViewEntity;
import com.portfolio.aips.project.interaction.view.repo.ViewRepository;

import com.portfolio.aips.project.url_service.archive.entity.ArchiveEntity;
import com.portfolio.aips.project.url_service.archive.repo.ArchiveRepository;
import com.portfolio.aips.project.url_service.common.entity.URLStatusEntity;
import com.portfolio.aips.project.url_service.common.service.url_generator.enums.URLGeneratorType;
import com.portfolio.aips.project.utils.DateUtils;
import com.portfolio.aips.project.utils.enums.LLMType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;




@SpringBootTest
class RatingSchedulerServiceTest {

    @Autowired
    private RatingSchedulerService ratingSchedulerService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private ArchiveELService archiveELService;

    @Autowired
    private ArchiveRepository archiveRepository;

    @Autowired
    private ViewRepository viewRepository;



    private ArchiveEntity getDummyArchiveEntity(URLStatusEntity urlStatusEntity) {
        ArchiveEntity archiveEntity = new ArchiveEntity();
        archiveEntity.setTitle("AI 글 요약 서비스");
        archiveEntity.setSiteSlug("ai-summary22222");
        archiveEntity.setDescription("긴 글을 자동으로 요약해주는 AI 서비스입니다.");
        archiveEntity.setUserPk(2L);
        archiveEntity.setLlmType(LLMType.CHATGPT);
        archiveEntity.setUrlStatusEntity(urlStatusEntity);
        return archiveEntity;
    }




    private URLStatusEntity getDummyUrlStatus() {
        return URLStatusEntity.builder()
                .urlLink("https://chat.openai.com22")
                .urlType(URLGeneratorType.Archive)
                .build();
    }

    @Test
    void ratingSchedulerServiceTest() throws InterruptedException {


        ArchiveEntity archiveEntity= archiveRepository.save(getDummyArchiveEntity(getDummyUrlStatus()));

        List<ArchiveDocument> docs = List.of(
                ArchiveDocument.builder()
                        .pk(archiveEntity.getPk().toString())
                        .title("spring msa config server")
                        .description("ABC")
                        .popularityScore(1.0)
                        .usefulnessAvgScore(0.8)
                        .reliabilityAvgScore(0.9)
                        .funAvgScore(0.7)
                        .ratingCount(10L)
                        .tags(List.of("kafka", "graphql"))
                        .llmType("chatgpt")
                        .createdDateTime(DateUtils.getDateTimeNow())
                        .topic("IT")
                        .viewCount(0L)
                        .build()
        );

            for(ArchiveDocument document : docs) {
                archiveELService.save(document);
            }

        ratingService.save(new SaveCommand(archiveEntity.getPk(), BoardType.Archive, 4, 3, 5, 2L));
            viewRepository.save(new ViewEntity(null, archiveEntity.getPk(), BoardType.Archive, 4L));

        ratingSchedulerService.updateRatingAndPopularScore();
    }

}