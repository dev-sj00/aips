package com.portfolio.aips.project.interaction.rating.repo;

import com.portfolio.aips.project.interaction.rating.dto.RedisSaveRatingsKeyDTO;
import com.portfolio.aips.project.interaction.rating.dto.RedisSaveRatingsValueDTO;
import com.portfolio.aips.project.interaction.rating.entity.RatingEntity;

import java.util.List;

public interface RatingRedisRepository {
    void saveRatings(RedisSaveRatingsKeyDTO keyDto);

    List<RatingEntity> findAllWithScan();
}
