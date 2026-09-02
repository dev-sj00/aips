package com.portfolio.aips.project.elastic_search.view.view_el_bulk;

import com.portfolio.aips.project.elastic_search.view.view_el_bulk.command.UpdateViewCountProcCommand;

import java.io.IOException;
import java.util.List;

public interface ViewELBulkService {
    void updateViewCountProc(List<UpdateViewCountProcCommand> commands) throws IOException;
}
