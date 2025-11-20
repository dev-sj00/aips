package com.portfolio.aips.project.url_service.service.archive;

import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;
import com.portfolio.aips.project.tags.dto.CreateTagsCommand;
import com.portfolio.aips.project.tags.service.TagsService;
import com.portfolio.aips.project.url_service.archive.dto.request.CreateArchiveRequest;
import com.portfolio.aips.project.url_service.archive.entity.ArchiveEntity;
import com.portfolio.aips.project.url_service.archive.repo.ArchiveRepository;
import com.portfolio.aips.project.url_service.archive.service.archive.ArchiveServiceImpl;
import com.portfolio.aips.project.url_service.common.entity.URLStatusEntity;
import com.portfolio.aips.project.url_service.common.repo.URLStatusRepository;
import com.portfolio.aips.project.url_service.common.service.url_generator.URLGeneratorService;
import com.portfolio.aips.project.url_service.common.service.url_generator.enums.URLGeneratorType;
import com.portfolio.aips.project.users.dto.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional(isolation = Isolation.READ_UNCOMMITTED)
class ArchiveServiceTest {
    @InjectMocks
    private ArchiveServiceImpl archiveService;

    @Mock
    private ArchiveRepository archiveRepository;

    @Autowired
    private URLStatusRepository urlStatusRepository;

    @Mock
    private URLGeneratorService urlGeneratorService;




    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createArchive_success() {
        // given
        CreateArchiveRequest request = new CreateArchiveRequest("https://claude.ai/share/9b7cebf3-ca49-4d91-a7ff-937f71a73aa9", "Test Description", List.of("23", "23"), "323232");
        CustomUserDetails user = mock(CustomUserDetails.class);
        when(user.getPk()).thenReturn(7L);


        //long boardId = archiveService.createArchive(request, user);



        Optional<URLStatusEntity> urlStatusEntity = urlStatusRepository.findByIsCreatedAndUrlLink(false, request.urlLink());

        System.out.println(urlStatusEntity.get());

    }


}