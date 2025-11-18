package com.portfolio.aips.project.config.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    //  @Async 전용 스레드 풀: Playwright 통신 + JPA 저장을 격리

    private static final int MAX_HEADER_SIZE_BYTES = 16 * 1024; // 16KB (기본 8KB)

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4); // DB 커넥션 풀과 유사하게 제한하는 것이 안전함
        executor.setThreadNamePrefix("External-Check-");
        executor.initialize();
        return executor;
    }

    // WebClient 설정
    @Bean
    @Primary
    public WebClient playwrightWebClient() {
        HttpClient httpClient = HttpClient.create()
                // HttpDecoderSpec을 사용하여 maxHeaderSize만 설정
                .httpResponseDecoder(spec -> {
                    // maxHeaderSize 설정
                    spec.maxHeaderSize(MAX_HEADER_SIZE_BYTES);
                    return spec;
                });

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024)) // 10MB
                .build();



        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(strategies)
                .baseUrl("http://localhost:3000")
                .build();
    }


}
