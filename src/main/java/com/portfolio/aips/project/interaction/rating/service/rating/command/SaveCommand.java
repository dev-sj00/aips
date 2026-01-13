package com.portfolio.aips.project.interaction.rating.service.rating.command;

import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;
import com.portfolio.aips.project.interaction.enums.BoardType;


public record SaveCommand(
        Long boardPk,
        BoardType boardType,
        int usefulnessScore,
        int reliabilityScore,
        int funScore,
        Long raterUserPk
) {

    public SaveCommand {
        validateScore(usefulnessScore);
        validateScore(reliabilityScore);
        validateScore(funScore);
    }

    private static void validateScore(int score) {
        if (score < 1 || score > 5) {
            throw new CustomException(ErrorCode.INVALID_RATING_SCORE);
        }
    }
}