package com.portfolio.aips.project.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class CookieUtils {

    public Cookie getCookie(String cookieName, String cookieValue, String path,  int maxAge)
    {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath(path);

        cookie.setMaxAge(maxAge);

        return cookie;
    }

    public String extractCookieToken(HttpServletRequest request, String extractCookieName) {

        if(request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if(extractCookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }


        log.info("empty Cookie");
        return null;
    }

    public List<String> extractCookieTokenWithSplitting(HttpServletRequest request, String extractCookieName, String separator) {

        if(request.getCookies() == null) {
            log.info("empty Cookie");
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if(extractCookieName.equals(cookie.getName())) {
                return Arrays.asList(cookie.getValue().split(separator));
            }
        }



        return null;
    }

}
