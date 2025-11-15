package com.portfolio.aips.project.utils;

import com.portfolio.aips.project.utils.enums.LLMUrlPrefix;
import com.portfolio.aips.project.utils.enums.LLMValidBodyValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class UrlValidatorUtils {
/*
    @Qualifier("brotliRestTemplate")
    private final RestTemplate restTemplate;

    public UrlValidatorUtils(@Qualifier("brotliRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public boolean isUrlAccessible(String url) {
        try{


            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            log.info("response status code: {}", response.getStatusCode());

            if(!response.getStatusCode().is2xxSuccessful())
            {
                return false;
            }




            String body = response.getBody();

            if(body == null || body.isEmpty())
            {
                return false;
            }




            return response.getStatusCode().is2xxSuccessful();

        }catch (HttpStatusCodeException e)
        {
            log.error(e.getMessage());
            return false;

        } catch (Exception e) {
            log.error(String.valueOf(e));
            return false;
        }
    }

    private boolean getResultToValidUrl(String url, String body)
    {

        LLMUrlPrefix prefix = LLMUrlPrefix.valueOf(LLMUrlPrefix.findKeyByUrl(url));
        if(prefix == LLMUrlPrefix.GROK)
        {

            return body.contains(LLMValidBodyValue.GROK.getValue());

        }
        else if(prefix == LLMUrlPrefix.CHATGPT)
        {
            return body.contains(LLMValidBodyValue.GROK.getValue());
        }
        else{

        }


    }*/
}
