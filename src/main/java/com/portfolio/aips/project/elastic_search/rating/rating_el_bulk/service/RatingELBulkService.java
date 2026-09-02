package com.portfolio.aips.project.elastic_search.rating.rating_el_bulk.service;

import com.portfolio.aips.project.elastic_search.rating.rating_el_bulk.service.command.UpdateRatingAndPopularityScoreBulkProcCommand;

import java.util.List;

public interface RatingELBulkService {
    void updateRatingAndPopularityScoreBulkProc(List<UpdateRatingAndPopularityScoreBulkProcCommand> commands);
}
