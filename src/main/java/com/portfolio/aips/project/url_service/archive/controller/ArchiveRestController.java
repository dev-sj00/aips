package com.portfolio.aips.project.url_service.archive.controller;


import com.portfolio.aips.project.interaction.enums.BoardType;
import com.portfolio.aips.project.interaction.view.service.view.ViewService;
import com.portfolio.aips.project.interaction.view.service.view.command.CreateViewCommand;
import com.portfolio.aips.project.tags.dto.CreateTagsCommand;
import com.portfolio.aips.project.tags.service.TagsService;
import com.portfolio.aips.project.url_service.archive.dto.request.CreateArchiveRequest;
import com.portfolio.aips.project.url_service.archive.service.archive.ArchiveService;
import com.portfolio.aips.project.url_service.common.service.url_generator.enums.URLGeneratorType;
import com.portfolio.aips.project.users.dto.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/archive")
public class ArchiveRestController {
    private final ArchiveService archiveService;
    private final TagsService tagsService;
    private final ViewService viewService;

    @PostMapping
    public ResponseEntity<Long> createArchive(@RequestBody @Valid CreateArchiveRequest createArchiveRequest, @AuthenticationPrincipal CustomUserDetails customUserDetails)
    {
        long boardId = archiveService.createArchive(createArchiveRequest, customUserDetails);
        tagsService.createTagProc(new CreateTagsCommand(boardId, URLGeneratorType.Archive.name(), createArchiveRequest.tagNames()));
        viewService.createView(new CreateViewCommand(boardId, BoardType.Archive, 0L));

        return ResponseEntity.status(HttpStatus.CREATED).body(boardId);
    }


}
