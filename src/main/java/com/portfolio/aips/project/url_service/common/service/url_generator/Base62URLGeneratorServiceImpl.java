package com.portfolio.aips.project.url_service.common.service.url_generator;

import com.portfolio.aips.project.url_service.archive.repo.ArchiveRepository;
import com.portfolio.aips.project.url_service.common.service.url_generator.enums.URLGeneratorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
@Slf4j
public class Base62URLGeneratorServiceImpl implements URLGeneratorService {
    private final ArchiveRepository archiveRepository;

    private static final long MAX_MOD = 56800235584L; //62^6승 mod 연산 용


    @Override
    public String createUrlProc(long userPk, URLGeneratorType urlType) { //암호화 url 생성, 아카이브 url 생성

        String url;
        do {
            long seed = userPk ^ System.nanoTime() ^ ThreadLocalRandom.current().nextLong();
            long mod = seed % MAX_MOD;

            String code = Base62.encode(mod);
            url = getStarsWithByUrlType(urlType) + code;
            log.info("url {}", url);
        }while (existsByUrlType(url, urlType));

        return url; // -> /s/Ab23Cs, /p/32B97F
    }


    private boolean existsByUrlType(String url, URLGeneratorType urlType) {


        return switch (urlType) {
            case Archive -> archiveRepository.existsBySiteSlug(url);
            case Protector -> true; // 나중에 구현
        };
    }

    private String getStarsWithByUrlType(URLGeneratorType urlType) {
        return switch (urlType) {
            case Archive -> "/s/"; //share link
            case Protector -> "/p/"; //protected link
        };
    }

    private static class Base62
    {
        private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

        private static String encode(long value) {
            if (value == 0) return "0";

            StringBuilder sb = new StringBuilder();
            while (value > 0) {
                int remainder = (int) (value % 62);
                sb.append(BASE62.charAt(remainder));
                value /= 62;
            }
            return sb.reverse().toString();
        }
    }

}
