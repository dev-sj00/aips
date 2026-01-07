package com.portfolio.aips.project.interaction.rating.service.rating_validator;

import com.portfolio.aips.project.interaction.rating.service.rating_validator.command.SaveVerifyCommand;

public interface RatingValidatorService {
    void saveVerify(SaveVerifyCommand command);
}
