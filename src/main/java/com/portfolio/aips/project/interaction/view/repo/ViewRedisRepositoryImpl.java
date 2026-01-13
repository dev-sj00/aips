package com.portfolio.aips.project.interaction.view.repo;

import com.portfolio.aips.project.interaction.enums.BoardType;
import com.portfolio.aips.project.interaction.view.entity.ViewEntity;
import com.portfolio.aips.project.interaction.view.service.view.command.IncreaseViewCountCommand;
import com.portfolio.aips.project.interaction.view.dto.RedisDecreaseViewCountDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.KeyScanOptions;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class ViewRedisRepositoryImpl implements ViewRedisRepository{

    private final RedisTemplate<String, Object> redisTemplate;
    @Override
    public void increaseViewCount(IncreaseViewCountCommand command) {
        String viewCountKey = getViewCountKey(command.boardType(), command.boardPk());
        String viewDeDupKey = getViewDeDupKey(command);

        Boolean isFirstView = redisTemplate.opsForValue()
                .setIfAbsent(viewDeDupKey, "1", 1, TimeUnit.HOURS);

        if(Boolean.TRUE.equals(isFirstView)){
            redisTemplate.opsForValue().increment(viewCountKey);
        }




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
                .type("set")
                .build();


        redisTemplate.execute((RedisCallback<Void>) connection -> {
            try (Cursor<byte[]> cursor = connection.scan(options)) {
                while (cursor.hasNext()) {
                    byte[] keyBytes = cursor.next();
                    String key = new String(keyBytes);

                    String boardType = key.split(":")[2];
                    long boardPk = Long.parseLong(key.split(":")[3]);

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

    private String getViewDeDupKey(IncreaseViewCountCommand command) {
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
