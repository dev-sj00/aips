package com.portfolio.aips.project.elastic_search.rating.rating_el_bulk.service.command;


import com.portfolio.aips.project.interaction.common.enums.BoardType;

public record UpdateRatingAndPopularityScoreBulkProcCommand(BoardType boardType,
                                                            Long boardPk,
                                                            Double usefulnessAvgScore,
                                                            Double reliabilityAvgScore,
                                                            Double funAvgScore,
                                                            Long ratingCount,
                                                            Double popularityScore) {
}
