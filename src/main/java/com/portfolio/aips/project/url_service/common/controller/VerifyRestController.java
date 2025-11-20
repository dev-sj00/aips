package com.portfolio.aips.project.url_service.common.controller;

import com.portfolio.aips.project.url_service.common.dto.commnad.CreateURLStatusCommand;
import com.portfolio.aips.project.url_service.common.dto.request.VerifyRequest;
import com.portfolio.aips.project.url_service.common.dto.response.URLVerifyResponse;
import com.portfolio.aips.project.url_service.common.service.url_status.URLStatusService;
import com.portfolio.aips.project.url_service.common.service.url_validator.UrlValidatorService;
import com.portfolio.aips.project.url_service.common.service.url_validator.enums.URLValidatorServiceImplName;
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

@RequestMapping("/api/v1/url/verify")
@RestController
@RequiredArgsConstructor
@Slf4j
public class VerifyRestController {
    private final ApplicationContext context;
    private final URLStatusService urlStatusService;

    @PostMapping("")
    public CompletableFuture<URLVerifyResponse> urlVerifyRequest(@RequestBody @Valid VerifyRequest verifyRequest) throws ExecutionException, InterruptedException {
        String archiveLink = verifyRequest.urlLink();
        log.info("findByUrl {}", Objects.requireNonNull(URLValidatorServiceImplName.findByUrl(archiveLink)).getBeanName());
        String serviceName = String.valueOf(Objects.requireNonNull(URLValidatorServiceImplName.findByUrl(archiveLink)).getBeanName());

        UrlValidatorService urlValidatorService= context.getBean(serviceName, UrlValidatorService.class);

        CompletableFuture<String> httpFuture = urlValidatorService.requestHTTP(verifyRequest.urlLink());




        return httpFuture
                .thenApply(body -> {
                    // HTTP 요청이 완료되면 이 블록은 @Async 스레드에서 실행됩니다.
                    // 4. 유효성 검증
                    log.info("body {}", body);
                    boolean isSuccess = urlValidatorService.validProc(archiveLink, body);

                    if (!isSuccess) {
                        // 유효성 검증 실패 시 예외를 던져 체인을 실패시킵니다.
                        throw new CustomException(ErrorCode.URL_NOT_FOUND);
                    }

                    CreateURLStatusCommand createURLStatusCommand = new CreateURLStatusCommand(verifyRequest.urlLink(), verifyRequest.urlGeneratorType());
                    urlStatusService.createURLStatusProc(createURLStatusCommand);


                    return new URLVerifyResponse(true, "검증 완료");
                })
                .exceptionally(ex -> {
                    // 6. 예외 처리
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;

                    if (cause instanceof CustomException customEx) {
                        // CustomException 별도 처리
                        log.error("CustomException 발생: {}", customEx.getErrorCode());
                        return new URLVerifyResponse(false, customEx.getMessage());
                    } else {
                        log.error("비동기 처리 중 예외 발생", cause);
                        return new URLVerifyResponse(false, "검증 처리 중 서버 오류 발생");
                    }
                });







    }

}
