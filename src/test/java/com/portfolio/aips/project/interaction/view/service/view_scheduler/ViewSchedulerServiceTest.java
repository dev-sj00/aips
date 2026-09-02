package com.portfolio.aips.project.interaction.view.service.view_scheduler;

import com.portfolio.aips.project.elastic_search.archive.dto.ArchiveDocument;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el.ArchiveELService;
import com.portfolio.aips.project.interaction.common.enums.BoardType;
import com.portfolio.aips.project.interaction.view.repo.dto.request.IncreaseViewCountDTO;
import com.portfolio.aips.project.interaction.view.service.view.ViewService;
import com.portfolio.aips.project.utils.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;


@SpringBootTest
class ViewSchedulerServiceTest {

    @Autowired
    private ViewSchedulerService viewSchedulerService;

    @Autowired
    private ViewService viewService;


    @Autowired
    private  ArchiveELService archiveELService;

    @Test
    void viewSchedulerServiceTest() throws IOException, InterruptedException {

        List<ArchiveDocument> docs = List.of(
                ArchiveDocument.builder()
                        .pk("70")
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



        viewService.increaseViewCount(new IncreaseViewCountDTO(70L, BoardType.Archive, "2L", null));

        viewSchedulerService.updateViewCount();

    }

}