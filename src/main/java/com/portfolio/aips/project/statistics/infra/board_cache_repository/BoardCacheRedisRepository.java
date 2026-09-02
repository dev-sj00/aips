package com.portfolio.aips.project.statistics.infra.board_cache_repository;

import com.portfolio.aips.project.interaction.common.enums.BoardType;
import com.portfolio.aips.project.statistics.app.router.statistics_usecase.result.findAllSubmitCountsAndBoardType;
import com.portfolio.aips.project.statistics.domain.repo.BoardCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
public class BoardCacheRedisRepository implements BoardCacheRepository
{
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String INCREASE_SUBMIT_COUNT_KEY = "board:submit_count";

    @Override
    public void increaseSubmitCountByBoardType(BoardType boardType) {
        if (boardType == null) throw new IllegalArgumentException("boardType cannot be null");


        try {
            redisTemplate.opsForHash().increment(INCREASE_SUBMIT_COUNT_KEY, boardType.name(), 1L);
        } catch (Exception e) {
            log.error("Failed to increase submit count for {}", boardType, e);
        }


    }

    public List<findAllSubmitCountsAndBoardType> findAllSubmitCountsAndBoardType()
    {

        Map<Object, Object> submitCountsMap = redisTemplate.opsForHash().entries(INCREASE_SUBMIT_COUNT_KEY);

        List<findAllSubmitCountsAndBoardType> result = new ArrayList<>();
        submitCountsMap.forEach((key, value) -> {

           BoardType boardType = BoardType.valueOf(key.toString());
           Long submitCount = Long.valueOf(value.toString());

           result.add(new findAllSubmitCountsAndBoardType(boardType, submitCount));

        });

        return result;

    }
}
