package com.portfolio.aips.project.interaction.view.repo;

import com.portfolio.aips.project.interaction.view.entity.ViewEntity;

import java.util.List;

public interface ViewBatchRepository {
    void updateViewBatchProc(List<ViewEntity> viewEntities, int batchSize);
}
