package com.portfolio.aips.project.interaction.rating.service.rating.result;

import jakarta.persistence.Column;

public record FindOwnRatingsResult( int usefulnessScore,
                                    int reliabilityScore,
                                    int funScore) {
}
