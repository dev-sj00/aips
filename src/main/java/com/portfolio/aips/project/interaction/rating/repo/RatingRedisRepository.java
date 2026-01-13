package com.portfolio.aips.project.interaction.rating.repo;

import com.portfolio.aips.project.interaction.rating.dto.RedisSaveRatingsKeyDTO;
import com.portfolio.aips.project.interaction.rating.dto.RedisSaveRatingsValueDTO;

public interface RatingRedisRepository {
    void saveRatings(RedisSaveRatingsKeyDTO keyDto, RedisSaveRatingsValueDTO valueDto);
}
