package com.portfolio.aips.project.interaction.view.service.view_scheduler;

import com.portfolio.aips.project.elastic_search.archive.dto.ArchiveDocument;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el.ArchiveELService;
import com.portfolio.aips.project.interaction.enums.BoardType;
import com.portfolio.aips.project.interaction.view.repo.dto.request.IncreaseViewCountDTO;
import com.portfolio.aips.project.interaction.view.service.view.ViewService;
import com.portfolio.aips.project.interaction.view.service.view.command.CreateViewCommand;
import com.portfolio.aips.project.utils.DateUtils;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ViewSchedulerServiceTest {

    @Autowired
    private ViewSchedulerService viewSchedulerService;

    @Autowired
    private ViewService viewService;

    @Autowired
    private EntityManager em;

    @Autowired
    private  ArchiveELService archiveELService;

    @Test
    @Transactional
    void viewSchedulerServiceTest() throws IOException {

        List<ArchiveDocument> doc = Collections.singletonList(new ArchiveDocument() {{
            setPk("2");
            setTitle("spring msa config server");
            setDescription("ABC");
            setPopularityScore(1L);
            setTags(List.of("kafka", "graphql"));
            setLlmType("chatgpt");
            setCreatedDateTime(DateUtils.getDateTimeNow());
            setTopic("IT");
            setViewCount(0L);
        }});

        for(ArchiveDocument document : doc) {
            archiveELService.save(document);
        }


        viewService.createView(new CreateViewCommand(2L, BoardType.Archive, 0L));

        viewService.increaseViewCount(new IncreaseViewCountDTO(2L, BoardType.Archive, "2L", null));

        viewSchedulerService.updateViewCount();

    }

}