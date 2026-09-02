package com.portfolio.aips.project.interaction.sanction.app.service.active_sanction.result;

import com.portfolio.aips.project.interaction.sanction.domain.ActiveSanctionEntity;

import java.time.LocalDateTime;

public record FindActiveSanctionsResult(Long pk,
                                        Long targetUserPk,
                                        String targetUserNickname,
                                        LocalDateTime startDateTime,
                                        LocalDateTime endDateTime) {


    public static FindActiveSanctionsResult from(ActiveSanctionEntity e) {
        return new FindActiveSanctionsResult(
                e.getPk(), e.getTargetUserPk(), e.getTargetUser().getNickname(), e.getStartDateTime(), e.getEndDateTime()
        );
    }

}
