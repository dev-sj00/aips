package com.portfolio.aips.project.statistics.app.router.statistics_usecase;

import com.portfolio.aips.project.statistics.domain.repo.StatisticsRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@RequiredArgsConstructor
public class StatisticsFindRouter {

    private final List<StatisticsRepository<?, ?>> statisticsRepositories;


    @SuppressWarnings("unchecked")
    //병목 오면 jdbc나 jooq로 교체
    public <C, R> List<R> execute(C command) {
        return statisticsRepositories.stream()
                .filter(r -> r.commandType().isInstance(command))
                .findFirst()
                .map(r -> (StatisticsRepository<C, R>) r)
                .orElseThrow()
                .findAllStatisticsByCommand(command);
    }


}
