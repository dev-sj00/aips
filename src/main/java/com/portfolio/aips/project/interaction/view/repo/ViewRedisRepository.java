package com.portfolio.aips.project.interaction.view.repo;

import com.portfolio.aips.project.interaction.view.entity.ViewEntity;
import com.portfolio.aips.project.interaction.view.repo.dto.request.ExistsViewCountDTO;
import com.portfolio.aips.project.interaction.view.repo.dto.request.IncreaseViewCountDTO;
import com.portfolio.aips.project.interaction.view.repo.dto.request.RedisDecreaseViewCountDTO;
import com.portfolio.aips.project.interaction.view.repo.dto.request.SaveHeartBeatDTO;
import com.portfolio.aips.project.interaction.view.repo.dto.result.FindByHbKeyResult;

import java.util.List;

public interface ViewRedisRepository {
    void increaseViewCount(IncreaseViewCountDTO dto);

    @Deprecated
    void increaseViewCount(IncreaseViewCountDTO dto, long viewCount);
    @Deprecated
    boolean existsViewCount(ExistsViewCountDTO dto);


    FindByHbKeyResult findByHbKey(String hbKey);
    void deleteKey(String key);
    String saveHeartBeat(SaveHeartBeatDTO dto); //return HearBeat redis key
    void decreaseViewCount(RedisDecreaseViewCountDTO dto);
    List<ViewEntity> findAllWithScan();
}
