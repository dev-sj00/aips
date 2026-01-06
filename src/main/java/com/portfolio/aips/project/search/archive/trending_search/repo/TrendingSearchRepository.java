package com.portfolio.aips.project.search.archive.trending_search.repo;

import com.portfolio.aips.project.search.archive.trending_search.domain.TrendingSearchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrendingSearchRepository extends JpaRepository<TrendingSearchEntity, Long> {

}
