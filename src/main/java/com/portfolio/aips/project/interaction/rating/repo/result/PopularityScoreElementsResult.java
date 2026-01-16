package com.portfolio.aips.project.interaction.rating.repo.result;

import com.portfolio.aips.project.interaction.enums.BoardType;

public record PopularityScoreElementsResult(BoardType boardType,
                                            Long boardPk,
                                            Double usefulnessAvgScore,
                                            Double reliabilityAvgScore,
                                            Double funAvgScore,
                                            Long ratingCount,
                                            Long viewCount

) {
}
