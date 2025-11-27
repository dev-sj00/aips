package com.portfolio.aips.project.elastic_search.archive.service.archive_el_auto_complete;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface ArchiveELAutoCompleteService {

    List<String> autocomplete(String keyword) throws URISyntaxException, IOException;


}
