package com.portfolio.aips.project.archive.service.UrlValidator;

import com.portfolio.aips.project.utils.enums.LLMValidBodyValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service("ChatGPTValidator")
@Slf4j
public class ChatGPTValidatorServiceImpl implements UrlValidatorService{

    @Qualifier("DefaultValidator")
    private final UrlValidatorService defaultValidatorService;

    // DefaultValidatorService Bean이 UrlValidatorService 타입이면 자동 주입 가능
    public ChatGPTValidatorServiceImpl(@Qualifier("DefaultValidator") UrlValidatorService defaultValidator) {
        this.defaultValidatorService = defaultValidator;
    }


    @Override
    @Async
    public CompletableFuture<String> requestHTTP(String url) throws ExecutionException, InterruptedException {

        return CompletableFuture.completedFuture(defaultValidatorService.requestHTTP(url).get());
    }

    @Override
    public boolean validProc(String url, String body) {

        String uuid = url.substring(url.lastIndexOf("/") + 1);

        return !body.contains(LLMValidBodyValue.CHATGPT.getValue() +" " +uuid); //ex: Can't load shared conversation 69146690-9cb0-800f-b95b-c292ab39296f2
    }
}
