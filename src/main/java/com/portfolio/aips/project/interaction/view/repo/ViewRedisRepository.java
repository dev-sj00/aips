package com.portfolio.aips.project.interaction.view.repo;

import com.portfolio.aips.project.interaction.view.entity.ViewEntity;
import com.portfolio.aips.project.interaction.view.service.view.command.IncreaseViewCountCommand;
import com.portfolio.aips.project.interaction.view.dto.RedisDecreaseViewCountDTO;

import java.util.List;

public interface ViewRedisRepository {
    void increaseViewCount(IncreaseViewCountCommand command);
    void decreaseViewCount(RedisDecreaseViewCountDTO command);
    List<ViewEntity> findAllWithScan();
}
