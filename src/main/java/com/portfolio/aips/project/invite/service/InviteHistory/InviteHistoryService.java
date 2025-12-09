package com.portfolio.aips.project.invite.service.InviteHistory;

import com.portfolio.aips.project.invite.dto.command.AddHistoryCommand;
import com.portfolio.aips.project.invite.entity.InviteHistoryEntity;
import com.portfolio.aips.project.invite.enums.InviteType;

import java.util.List;

public interface InviteHistoryService {
    List<InviteHistoryEntity> findAllHistory(long ownerUserPk, InviteType targetType);
    void addHistory(AddHistoryCommand command);
    void deleteHistory(long invitePk, long targetUserPk);
}
