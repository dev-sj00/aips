package com.portfolio.aips.project.config.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;

@Configuration
public class RedisLuaConfig {



    @Bean(name = "archiveTrendingZsetEvictIfOverLimitScript")
    public DefaultRedisScript<Void> archiveTrendingZsetEvictIfOverLimitScript() {
        DefaultRedisScript<Void> script = new DefaultRedisScript<>();
        script.setLocation(
                new ClassPathResource(
                        "redis/archive_trending_zset_evict_if_over_limit.lua"
                )
        );
        script.setResultType(Void.class);
        return script;
    }

}
