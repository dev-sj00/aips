package com.portfolio.aips.project.interaction.rating.repo.result;

public record BoardAvgRatingScoreResult(String boardType,
                                        Long boardPk,
                                        Double usefulnessScore,
                                        Double reliabilityScore,
                                        Double funScore) {
}
