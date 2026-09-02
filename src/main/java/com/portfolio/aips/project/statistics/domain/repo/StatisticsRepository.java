package com.portfolio.aips.project.statistics.domain.repo;


import org.springframework.data.domain.Page;

import java.util.List;

public interface StatisticsRepository<C, R> {
     List<R> findAllStatisticsByCommand(C command);
     Page<R> findAllStatisticsByCommandWithOffsetAndLimit(C command);
     Class<C> commandType();
}
