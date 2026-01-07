package com.portfolio.aips.project.interaction.rating.repo;

import com.portfolio.aips.project.interaction.rating.entity.RatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<RatingEntity, Long> {
}
