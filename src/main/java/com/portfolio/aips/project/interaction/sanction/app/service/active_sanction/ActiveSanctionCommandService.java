package com.portfolio.aips.project.interaction.sanction.app.service.active_sanction;

import com.portfolio.aips.project.interaction.report.domain.model.BanType;
import com.portfolio.aips.project.interaction.sanction.app.service.active_sanction.command.UpdateActiveSanctionCommand;

public interface ActiveSanctionCommandService {
    void createActiveSanction(BanType banType, Long targetUserPk);

    //밴 바꾸기
    void updateActiveSanction(UpdateActiveSanctionCommand command);

    void deleteAllExpiredActiveSanction();

}
