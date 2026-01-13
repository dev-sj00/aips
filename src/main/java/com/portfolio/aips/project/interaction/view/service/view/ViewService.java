package com.portfolio.aips.project.interaction.view.service.view;

import com.portfolio.aips.project.interaction.view.service.view.command.IncreaseViewCountCommand;

public interface ViewService {
    void increaseViewCount(IncreaseViewCountCommand command);
}
