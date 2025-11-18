package com.portfolio.aips.project.url_service.archive.controller;

import com.portfolio.aips.project.url_service.archive.dto.request.CreateArchiveRequest;
import com.portfolio.aips.project.url_service.service.url_validator.UrlValidatorService;
import com.portfolio.aips.project.url_service.service.url_validator.enums.URLValidatorServiceImplName;
import com.portfolio.aips.project.url_service.archive.service.archive.ArchiveService;
import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;
import com.portfolio.aips.project.users.dto.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RequestMapping("/api/v1/archive")
@RestController
@RequiredArgsConstructor
@Slf4j
public class ArchiveRestController {
    private final ArchiveService archiveService;
    private final ApplicationContext context;;

    @PostMapping("")
    public CompletableFuture<String> createArchivedRequest(@RequestBody @Valid CreateArchiveRequest createArchiveRequest, @AuthenticationPrincipal CustomUserDetails customUserDetails) throws ExecutionException, InterruptedException {
        String archiveLink = createArchiveRequest.archiveLink();
        log.info("findByUrl {}", Objects.requireNonNull(URLValidatorServiceImplName.findByUrl(archiveLink)).getBeanName());
        String serviceName = String.valueOf(Objects.requireNonNull(URLValidatorServiceImplName.findByUrl(archiveLink)).getBeanName());

        UrlValidatorService urlValidatorService= context.getBean(serviceName, UrlValidatorService.class);

        CompletableFuture<String> httpFuture = urlValidatorService.requestHTTP(createArchiveRequest.archiveLink());




        return httpFuture
                .thenApply(body -> {
                    // HTTP 요청이 완료되면 이 블록은 @Async 스레드에서 실행됩니다.

                    // 4. 유효성 검증
                    log.info("body {}", body);
                    boolean isSuccess = urlValidatorService.validProc(archiveLink, body);

                    if (!isSuccess) {
                        // 유효성 검증 실패 시 예외를 던져 체인을 실패시킵니다.
                        throw new CustomException(ErrorCode.ARCHIVE_URL_NOT_FOUND);
                    }

                    // 5. 검증 성공 시, archiveService의 JPA 로직을 비동기 체인에 연결
                    // archiveService.createArchive는 동기 메서드이므로, runArchiveService 래퍼를 사용
                    // 래퍼가 내부적으로 별도 스레드에서 archiveService를 실행하고 완료를 기다립니다.

                        archiveService.createArchive(createArchiveRequest, customUserDetails);

                        return "성공";
                })
                .exceptionally(ex -> {
                    // 6. 예외 처리
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;

                    if (cause instanceof CustomException customEx) {
                        // CustomException 별도 처리
                        log.error("CustomException 발생: {}", customEx.getErrorCode());
                        throw customEx;
                    } else {
                        log.error("비동기 처리 중 예외 발생", cause);
                        return "검증 오류";
                    }
                });







    }

}
