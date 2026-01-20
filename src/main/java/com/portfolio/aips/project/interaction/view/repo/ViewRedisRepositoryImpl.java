package com.portfolio.aips.project.interaction.view.repo;

import com.portfolio.aips.project.interaction.enums.BoardType;
import com.portfolio.aips.project.interaction.view.entity.ViewEntity;
import com.portfolio.aips.project.interaction.view.repo.dto.request.ExistsViewCountDTO;
import com.portfolio.aips.project.interaction.view.repo.dto.request.IncreaseViewCountDTO;
import com.portfolio.aips.project.interaction.view.repo.dto.request.RedisDecreaseViewCountDTO;
import com.portfolio.aips.project.interaction.view.repo.dto.request.SaveHeartBeatDTO;
import com.portfolio.aips.project.interaction.view.repo.dto.result.FindByHbKeyResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.KeyScanOptions;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ViewRedisRepositoryImpl implements ViewRedisRepository{

    private final RedisTemplate<String, Object> redisTemplate;

    private static final Duration HEARTBEAT_TTL = Duration.ofMinutes(3);
    private static final RedisScript<Long> INCR_WITH_TTL =
            RedisScript.of("""
            local v = redis.call("INCR", KEYS[1])
            redis.call("EXPIRE", KEYS[1], ARGV[1])
            return v
        """, Long.class);


    @Override
    public void increaseViewCount(IncreaseViewCountDTO dto) {
        String viewCountKey = getViewCountKey(dto.boardType(), dto.boardPk());
        String viewDeDupKey = getViewDeDupKey(dto);


        log.info("{}, {}, {}, {}", dto.ViewerIpAddr(), dto.boardPk(), viewCountKey, viewDeDupKey);
        Boolean isFirstView = redisTemplate.opsForValue()
                .setIfAbsent(viewDeDupKey, "1", 6, TimeUnit.HOURS);

        if(Boolean.TRUE.equals(isFirstView)){
            redisTemplate.execute(
                    INCR_WITH_TTL,
                    List.of(viewCountKey),
                    300
            );
        }else {
            log.info("중복임");

        }

    }



    @Override
    @Deprecated
    public void increaseViewCount(IncreaseViewCountDTO dto, long viewCount) {
        String viewCountKey = getViewCountKey(dto.boardType(), dto.boardPk());
        String viewDeDupKey = getViewDeDupKey(dto);


        log.info("{}, {}, {}, {}", dto.ViewerIpAddr(), dto.boardPk(), viewCountKey, viewDeDupKey);
        Boolean isFirstView = redisTemplate.opsForValue()
                .setIfAbsent(viewDeDupKey, "1", 6, TimeUnit.HOURS);


        if(Boolean.TRUE.equals(isFirstView)){
            redisTemplate.opsForValue().increment(viewCountKey, viewCount);
        }else {
            log.info("중복임");

        }

    }

    @Deprecated
    public boolean existsViewCount(ExistsViewCountDTO dto) {
        String viewCountKey = getViewCountKey(dto.boardType(), dto.boardPk());

        return  redisTemplate.hasKey(viewCountKey);

    }



    @Override
    public String saveHeartBeat(SaveHeartBeatDTO dto) {
        String hbKey = getHeartBeatKey(dto);

        long now = System.currentTimeMillis();

        redisTemplate.opsForList().rightPush(hbKey, String.valueOf(now));
        redisTemplate.expire(hbKey, HEARTBEAT_TTL);

       /* if (last - first >= HEARTBEAT_TIME_PERIOD) {
            return true;
        }*/

        return hbKey;

    }

    public FindByHbKeyResult findByHbKey(String hbKey) {
        Long size = redisTemplate.opsForList().size(hbKey);

        log.info("size: {}", size);
        if(size == null || size < 2)
        {

            return new FindByHbKeyResult(null,null);
        }


        List<Object> times = redisTemplate.opsForList().range(hbKey, 0, -1);

        if(times == null || times.size() < 2)
        {
            return new FindByHbKeyResult(size, null);
        }

        return new FindByHbKeyResult(size, times);
    }

    public void deleteKey(String key)
    {
        redisTemplate.delete(key);
    }

    @Override
    public void decreaseViewCount(RedisDecreaseViewCountDTO command) {

        redisTemplate.opsForValue().decrement(getViewCountKey(command.boardType(), command.boardPk()));
    }

    @Override
    public List<ViewEntity> findAllWithScan() {
        List<ViewEntity> entities = new ArrayList<>();


        KeyScanOptions options = (KeyScanOptions) KeyScanOptions.scanOptions()
                .match("view:count:b_type:*:b_pk:*")
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

                    String viewCounts =
                            new String(Objects.requireNonNull(connection.stringCommands().get(keyBytes)));

                    entities.add(getViewEntity(boardPk, boardType, viewCounts));
                }
            }
            return null;
        });

        return entities;
    }
    private ViewEntity getViewEntity(long boardPk, String boardType, String viewCounts) {

        return ViewEntity
                .builder()
                .boardPk(boardPk)
                .boardType(BoardType.valueOf(boardType))
                .viewCount(Long.parseLong(viewCounts))
                .build();
    }

    private String getViewCountKey(BoardType boardType, long boardPk) {

        return "view:count:b_type:"+ boardType+":b_pk:"+boardPk;
    }

    private String getHeartBeatKey(SaveHeartBeatDTO command) {

        return "view:hb:ip:"+command.ipAddr()+":"+command.userAgent();
    }

    private String getViewDeDupKey(IncreaseViewCountDTO command) {
        if (command.ViewerUserPk() != null) {
            // 로그인 유저
            return "View:dedup:b_type:" + command.boardType()
                    + ":b_pk:" + command.boardPk()
                    + ":u_pk:" + command.ViewerUserPk();
        } else {
            // 비로그인 유저
            return "View:dedup:b_type:" + command.boardType()
                    + ":b_pk:" + command.boardPk()
                    + ":u_ip:" + command.ViewerIpAddr();
        }
    }

}
