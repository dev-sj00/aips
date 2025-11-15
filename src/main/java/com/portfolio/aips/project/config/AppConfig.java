package com.portfolio.aips.project.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class AppConfig {

    @Bean
    @Primary
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


/*    @Bean
    @Qualifier("brotliRestTemplate")
    public RestTemplate createBrotliRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BrotliUtils.BrotliDecompressionInterceptor());
        return restTemplate;
    }*/





}
