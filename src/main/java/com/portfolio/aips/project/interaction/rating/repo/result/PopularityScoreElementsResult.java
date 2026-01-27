package com.portfolio.aips.project.interaction.rating.repo.result;

import com.portfolio.aips.project.interaction.common.enums.BoardType;

import java.time.LocalDateTime;

public record PopularityScoreElementsResult(BoardType boardType,
                                            Long boardPk,
                                            Double usefulnessAvgScore,
                                            Double reliabilityAvgScore,
                                            Double funAvgScore,
                                            Long ratingCount,
                                            Long viewCount,
                                            LocalDateTime createdDateTime

) {
}
