package com.portfolio.aips.project.search.archive.trending_search.service;

import com.portfolio.aips.project.search.archive.trending_search.domain.TrendingSearchEntity;

import java.io.IOException;
import java.net.URISyntaxException;

public interface TrendingSearchScheduleService {
    void save() throws IOException, URISyntaxException;
}
