package com.portfolio.aips.project.interaction.rating.repo;

import com.portfolio.aips.project.interaction.rating.dto.RedisSaveRatingsKeyDTO;
import com.portfolio.aips.project.interaction.rating.dto.RedisSaveRatingsValueDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RatingRedisRepositoryImpl implements RatingRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void saveRatings(RedisSaveRatingsKeyDTO keyDto, RedisSaveRatingsValueDTO valueDto) {
        String key = "rating:b_type:"+keyDto.boardType()+":b_pk:"+keyDto.boardPk();
        redisTemplate.opsForList().rightPush(key, valueDto);
    }
}
