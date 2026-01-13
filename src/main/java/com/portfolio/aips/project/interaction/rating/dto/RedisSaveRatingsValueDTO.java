package com.portfolio.aips.project.interaction.rating.dto;

public record RedisSaveRatingsValueDTO(   int usefulnessScore,
                                          int reliabilityScore,
                                          int funScore) {
}
