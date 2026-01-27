package com.portfolio.aips.project.interaction.sanction.app;

import com.portfolio.aips.project.interaction.report.domain.model.BanType;

public interface ActiveSanctionService {
    void createActiveSanction(BanType banType, Long targetUserPk);
}
