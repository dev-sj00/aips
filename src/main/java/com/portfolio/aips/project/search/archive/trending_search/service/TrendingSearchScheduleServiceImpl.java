package com.portfolio.aips.project.search.archive.trending_search.service;


import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.command.GetTrendingKeywordsCommand;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.enums.SearchDateRange;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.repo.ArchiveELTrendingSearchLogElasticRepository;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.service.TrendingScoreCalculator.TrendingScoreCalculator;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.service.TrendingScoreCalculator.command.CalculateScoreCommand;
import com.portfolio.aips.project.search.archive.trending_search.domain.TrendingSearchEntity;
import com.portfolio.aips.project.search.archive.trending_search.repo.TrendingSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrendingSearchScheduleServiceImpl implements TrendingSearchScheduleService {

    private final ArchiveELTrendingSearchLogElasticRepository archiveELTrendingSearchLogElasticRepository;
    private final TrendingScoreCalculator trendingScoreCalculator;
    private final TrendingSearchRepository trendingSearchRepository;

    @Override
    @Scheduled(cron = "0 0 3 ? * MON")
    @Transactional

    public void save() throws IOException, URISyntaxException {
        HashMap<String, CalculateScoreCommand> map =  archiveELTrendingSearchLogElasticRepository.findBySearchDateRangeAndCommand(SearchDateRange.WEEK, new GetTrendingKeywordsCommand(80));

        List<TrendingSearchEntity> entities = new ArrayList<>();
        map.forEach((key, command) -> {
            double score = trendingScoreCalculator.calculateScore(command);
            log.info("score : {}", score);
            log.info("command : current : {} prev: {}", command.currentDocCount(), command.prevDocCount());
            TrendingSearchEntity trendingSearchEntity = new TrendingSearchEntity();
            trendingSearchEntity.setScore(score);
            trendingSearchEntity.setKeyword(key);
            trendingSearchEntity.setDocCount(command.currentDocCount());

            entities.add(trendingSearchEntity);
        });

        trendingSearchRepository.saveAll(entities);
    }
}
