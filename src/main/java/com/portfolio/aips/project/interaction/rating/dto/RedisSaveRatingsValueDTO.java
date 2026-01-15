package com.portfolio.aips.project.interaction.rating.dto;


@Deprecated
public record RedisSaveRatingsValueDTO(   int usefulnessScore,
                                          int reliabilityScore,
                                          int funScore) {
}
