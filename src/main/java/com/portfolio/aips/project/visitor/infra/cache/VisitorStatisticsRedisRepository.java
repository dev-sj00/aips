package com.portfolio.aips.project.visitor.infra.cache;

import com.portfolio.aips.project.visitor.domain.repo.VisitorCacheRepository;
import com.portfolio.aips.project.visitor.infra.persistence.VisitorStatisticsJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class VisitorStatisticsRedisRepository implements VisitorCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String VISITOR_ALL_KEY = "visitor:all:";

    @Override
    public void save(Long userPk) {

        redisTemplate.opsForHyperLogLog().add(VISITOR_ALL_KEY, userPk);

    }

    @Override
    public Long findAll() {

       return Optional.of(redisTemplate.opsForHyperLogLog().size(VISITOR_ALL_KEY)).orElse(0L);
    }
}
