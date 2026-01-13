package com.portfolio.aips.project.interaction.view.service.view;

import com.portfolio.aips.project.interaction.view.repo.ViewRepository;
import com.portfolio.aips.project.interaction.view.service.view.command.IncreaseViewCountCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ViewServiceImpl implements ViewService {
    private final ViewRepository viewRepository;

    @Override
    public void increaseViewCount(IncreaseViewCountCommand command) {


    }
}
