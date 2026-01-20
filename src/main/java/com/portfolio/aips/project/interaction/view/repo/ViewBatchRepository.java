package com.portfolio.aips.project.interaction.view.repo;

import com.portfolio.aips.project.interaction.view.entity.ViewEntity;
import com.portfolio.aips.project.interaction.view.repo.dto.request.FindAllViewBatchProcDTO;
import com.portfolio.aips.project.interaction.view.repo.dto.result.FindAllViewBatchProcResult;

import java.util.List;

public interface ViewBatchRepository {
    void updateViewBatchProc(List<ViewEntity> viewEntities, int batchSize) throws InterruptedException;
    List<FindAllViewBatchProcResult> findAllViewBatchProc(List<ViewEntity> viewEntities,  int batchSize) throws InterruptedException;
}
