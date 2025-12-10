package com.portfolio.aips.project.invite.service.InviteHistory;

import com.portfolio.aips.project.invite.entity.InviteHistoryEntity;
import com.portfolio.aips.project.invite.enums.InviteType;
import com.portfolio.aips.project.invite.service.InviteHistory.command.DeleteHistoryCommand;

import java.util.List;

public interface InviteHistoryService {
    List<InviteHistoryEntity> findAllHistory(long ownerUserPk, InviteType targetType);
    void deleteHistory(DeleteHistoryCommand deleteHistoryCommand);


}
