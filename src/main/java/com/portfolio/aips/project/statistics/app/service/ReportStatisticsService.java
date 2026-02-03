package com.portfolio.aips.project.statistics.app.service;

import com.portfolio.aips.project.statistics.app.service.command.FindAllStatisticsCommand;
import com.portfolio.aips.project.statistics.app.service.result.FindAllStatisticsResult;
import com.portfolio.aips.project.statistics.domain.repo.ReportStatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportStatisticsService {

    private final ReportStatisticsRepository reportStatisticsRepository;

    public List<FindAllStatisticsResult> findAllStatisticsProc(FindAllStatisticsCommand command)
    {
        return reportStatisticsRepository.findAllStatisticsByCommand(command);
    }
}
