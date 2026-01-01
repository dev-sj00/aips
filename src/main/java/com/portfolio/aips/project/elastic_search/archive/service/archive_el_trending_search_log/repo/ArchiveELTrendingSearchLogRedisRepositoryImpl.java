package com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.repo;

import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.command.RedisSaveCommand;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.enums.SearchDateRange;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.result.GetTrendingKeywordsResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.*;


@Repository
@RequiredArgsConstructor
public class ArchiveELTrendingSearchLogRedisRepositoryImpl implements ArchiveELTrendingSearchLogRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void save(RedisSaveCommand command) {
        String hashKey = getKey(command.range(), command.keyword());
        String zSetKey = getKey(command.range(), "zSet");

        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();

        zSetOps.add(zSetKey, hashKey, command.score());

        HashOperations<String, Object, Object> hashOps = redisTemplate.opsForHash();
        hashOps.put(hashKey, "keyword", command.keyword());
        hashOps.put(hashKey, "docCount", command.docCount());


        //해당 키워드가 레디스에 있을 시 덮어씌우지 않음
        hashOps.putIfAbsent(hashKey, "createDateTime", command.createDateTime());


        Long ttl = redisTemplate.getExpire(hashKey);

        if (ttl == -1) {
            redisTemplate.expire(hashKey, getTTL(command.range()));
            redisTemplate.expire(zSetKey, getTTL(command.range()));
        }




    }







    private Duration getTTL(SearchDateRange range) {

        return switch (range) {
            case DAILY -> Duration.ofHours(3);
            case WEEK -> Duration.ofDays(1);
            case THREE_DAYS -> Duration.ofHours(12);
            default -> throw new IllegalArgumentException("Unknown range: " + range);
        };
    }


    private String getKey(SearchDateRange range, String key) {

        return  switch (range) {
            case DAILY -> "trending:daily:" + key;
            case WEEK -> "trending:week:" + key;
            case THREE_DAYS -> "trending:3days:" + key;
            default -> throw new IllegalArgumentException("Unknown range: " + range);
        };
    }

    @Override
    public List<GetTrendingKeywordsResult> findAll(SearchDateRange range)
    {
        String zSetKey = getKey(range, "zSet");


        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        HashOperations<String, Object, Object> hashOps = redisTemplate.opsForHash();

        // ZSet에 있는 모든 hashKey 조회
        Set<ZSetOperations.TypedTuple<Object>> tuples =
                zSetOps.reverseRangeWithScores(zSetKey, 0, -1);

        if(tuples == null || tuples.isEmpty())
        {
            return Collections.emptyList();
        }

        List<GetTrendingKeywordsResult> result = new ArrayList<>();


        int resultIdx = 0;
        for(ZSetOperations.TypedTuple<Object> tuple : tuples)
        {
            String hashKey = (String) tuple.getValue();

            assert hashKey != null;
            Map<Object, Object> fields = hashOps.entries(hashKey);

            GetTrendingKeywordsResult dto = new GetTrendingKeywordsResult(
                    resultIdx++,
                    fields.get("keyword").toString(),
                    fields.get("docCount").toString(),
                    fields.get("createDateTime").toString()

            );
            result.add(dto);
        }

        return result;
    }


}
