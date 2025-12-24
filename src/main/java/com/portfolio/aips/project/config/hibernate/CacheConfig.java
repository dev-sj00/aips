package com.portfolio.aips.project.config.hibernate;

import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            cm.createCache("invitePolicyCache",
                    new MutableConfiguration<>()
                            .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.ONE_HOUR))
                            .setStoreByValue(false)
                            .setStatisticsEnabled(true)
            );

            cm.createCache("userNicknameCache",
                    new MutableConfiguration<>()
                            .setExpiryPolicyFactory(
                                    CreatedExpiryPolicy.factoryOf(Duration.TEN_MINUTES))
                            .setStoreByValue(false)
                            .setStatisticsEnabled(true)
            );
        };
    }
}
