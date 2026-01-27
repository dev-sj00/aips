package com.portfolio.aips.project.interaction.rating.repo;

import com.portfolio.aips.project.interaction.common.enums.BoardType;
import com.portfolio.aips.project.interaction.rating.dto.RedisSaveRatingsKeyDTO;
import com.portfolio.aips.project.interaction.rating.repo.result.FindAllWithScanResult;
import com.portfolio.aips.project.interaction.common.repo.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.KeyScanOptions;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class RatingRedisRepositoryImpl implements RatingRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final BoardRepository boardRepository;

    @Override
    public void saveRatings(RedisSaveRatingsKeyDTO keyDto) {
        String key = "rating:recalc:b_type:"+keyDto.boardType()+":b_pk:"+keyDto.boardPk();

        Object value = redisTemplate.opsForValue().get(key);

        if(value==null){
            LocalDateTime createdDateTime = boardRepository.findByBoardPkAndBoardTypes(keyDto.boardPk(), keyDto.boardType());


            redisTemplate.opsForValue()
                    .set(key, createdDateTime.toString());
        }
    }

    public List<FindAllWithScanResult> findAllWithScan()
    {
        List<FindAllWithScanResult> entities = new ArrayList<>();


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

                    String boardType = key.split(":")[3];
                    long boardPk = Long.parseLong(key.split(":")[5]);

                    LocalDateTime createdDateTime = LocalDateTime.parse
                            (Objects.requireNonNull
                                    (redisTemplate.opsForValue().get(key))
                                    .toString());



                    entities.add(getFindAllWithScanResult(boardPk, BoardType.valueOf(boardType), createdDateTime));
                }
            }
            return null;
        });

        return entities;
    }

    private FindAllWithScanResult getFindAllWithScanResult(long boardPk, BoardType boardType, LocalDateTime createdDateTime) {
        return new FindAllWithScanResult(boardPk, boardType, createdDateTime);
    }
}
