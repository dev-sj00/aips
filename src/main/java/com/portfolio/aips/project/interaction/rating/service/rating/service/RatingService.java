package com.portfolio.aips.project.interaction.rating.service.rating.service;

import com.portfolio.aips.project.interaction.rating.service.rating.command.SaveCommand;

public interface RatingService {
    void save(SaveCommand command);
}
