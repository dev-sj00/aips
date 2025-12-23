package com.portfolio.aips.project.elastic_search.archive.service.archive_el_search.command;

import com.portfolio.aips.project.elastic_search.archive.service.archive_el_search.enums.SearchDateRange;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_search.enums.SearchSortType;

public record SearchByConditionCommand(String keyword, String topic, SearchSortType sortType, Integer pageFrom, Integer pageSize, SearchDateRange dateRange, String llmType) {
}
