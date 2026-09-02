package com.portfolio.aips.project.interaction.sanction.app.service.active_sanction;

import com.portfolio.aips.project.interaction.sanction.app.service.active_sanction.result.FindActiveSanctionsResult;
import org.springframework.data.domain.Page;

public interface ActiveSanctionQueryService {
    Page<FindActiveSanctionsResult> findAllActiveSanctions(int page, int size);
    FindActiveSanctionsResult findActiveSanctionsByUserPk(Long userPk);

}
