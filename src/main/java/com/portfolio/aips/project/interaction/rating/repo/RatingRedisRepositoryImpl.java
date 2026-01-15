package com.portfolio.aips.project.interaction.rating.repo;

import com.portfolio.aips.project.interaction.enums.BoardType;
import com.portfolio.aips.project.interaction.rating.dto.RedisSaveRatingsKeyDTO;
import com.portfolio.aips.project.interaction.rating.dto.RedisSaveRatingsValueDTO;
import com.portfolio.aips.project.interaction.rating.entity.RatingEntity;
import com.portfolio.aips.project.interaction.view.entity.ViewEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.KeyScanOptions;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class RatingRedisRepositoryImpl implements RatingRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void saveRatings(RedisSaveRatingsKeyDTO keyDto) {
        String key = "rating:recalc:b_type:"+keyDto.boardType()+":b_pk:"+keyDto.boardPk();
        redisTemplate.opsForSet().add(key, "1");
    }

    public List<RatingEntity> findAllWithScan()
    {
        List<RatingEntity> entities = new ArrayList<>();


        KeyScanOptions options = (KeyScanOptions) KeyScanOptions.scanOptions()
                .match("rating:recalc:b_type:*:b_pk:*")
                .count(1000)
                .type("string")
                .build();


        redisTemplate.execute((RedisCallback<Void>) connection -> {
            try (Cursor<byte[]> cursor = connection.scan(options)) {
                while (cursor.hasNext()) {
                    byte[] keyBytes = cursor.next();
                    String key = new String(keyBytes);

                    String boardType = key.split(":")[2];
                    long boardPk = Long.parseLong(key.split(":")[4]);


                    entities.add(getRatingEntity(boardPk, BoardType.valueOf(boardType)));
                }
            }
            return null;
        });

        return entities;
    }

    private RatingEntity getRatingEntity(long boardPk, BoardType boardType) {
        return RatingEntity
                .builder()
                .boardPk(boardPk)
                .boardType(boardType)
                .build();
    }
}
