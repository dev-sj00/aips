package com.portfolio.aips.project.interaction.view.service.view;

import com.portfolio.aips.project.interaction.view.repo.dto.request.IncreaseViewCountDTO;
import com.portfolio.aips.project.interaction.view.repo.dto.request.SaveHeartBeatDTO;

public interface ViewService {
    void increaseViewCount(IncreaseViewCountDTO command);
    boolean isHeartBeatValid(SaveHeartBeatDTO command);
}
