package com.portfolio.aips.project.archive.service.UrlValidator;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface UrlValidatorService {

    CompletableFuture<String> requestHTTP(String url) throws ExecutionException, InterruptedException;
    boolean validProc(String url, String body);
}
