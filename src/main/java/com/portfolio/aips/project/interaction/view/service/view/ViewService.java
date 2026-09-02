package com.portfolio.aips.project.interaction.view.service.view;

import com.portfolio.aips.project.interaction.view.repo.dto.request.IncreaseViewCountDTO;
import com.portfolio.aips.project.interaction.view.repo.dto.request.SaveHeartBeatDTO;
import com.portfolio.aips.project.interaction.view.service.view.command.CreateViewCommand;

public interface ViewService {
    void increaseViewCount(IncreaseViewCountDTO dto); //redis repo 동일
    boolean isHeartBeatValid(SaveHeartBeatDTO dto); //redis repo 동일
    void createView(CreateViewCommand command);
}
