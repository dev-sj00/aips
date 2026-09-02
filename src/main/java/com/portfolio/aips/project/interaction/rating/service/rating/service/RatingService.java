package com.portfolio.aips.project.interaction.rating.service.rating.service;

import com.portfolio.aips.project.interaction.rating.entity.RatingEntity;
import com.portfolio.aips.project.interaction.rating.service.rating.command.FindOwnRatings;
import com.portfolio.aips.project.interaction.rating.service.rating.command.SaveCommand;
import com.portfolio.aips.project.interaction.rating.service.rating.result.FindOwnRatingsResult;

public interface RatingService {
    void save(SaveCommand command);
    FindOwnRatingsResult findOwnRatings(FindOwnRatings command);
    
}
