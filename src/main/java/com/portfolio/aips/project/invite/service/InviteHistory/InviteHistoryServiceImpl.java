package com.portfolio.aips.project.invite.service.InviteHistory;

import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;

import com.portfolio.aips.project.invite.entity.InviteEntity;
import com.portfolio.aips.project.invite.entity.InviteHistoryEntity;
import com.portfolio.aips.project.invite.enums.InviteType;
import com.portfolio.aips.project.invite.repo.InviteHistoryRepository;
import com.portfolio.aips.project.invite.repo.InviteRepository;
import com.portfolio.aips.project.invite.service.InviteHistory.command.DeleteHistoryCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InviteHistoryServiceImpl implements InviteHistoryService {

    private final InviteRepository inviteRepository;
    private final InviteHistoryRepository inviteHistoryRepository;


    @Override
    public List<InviteHistoryEntity> findAllHistory(long ownerUserPk, InviteType targetType) {
        InviteEntity inviteEntity = inviteRepository.findWithHistoryByOwnerUserPkAndTargetType(ownerUserPk, targetType)
                .orElseThrow(() ->
                {
                    log.error("pk : {} targetType : {} NOT FOUND InviteEntity ", ownerUserPk, targetType);
                    return new CustomException(ErrorCode.INTERNAL_SERVER_ERROR); // 사용자 잘못이 아님
                });

        return inviteEntity.getInviteHistory();
    }


    @Transactional
    public void deleteHistory(DeleteHistoryCommand command) {
        int isDelete = inviteHistoryRepository.deleteHistoryByOwnerPkAndInviteTypeAndHistoryPk(command.ownerUserPk(), command.targetType(), command.targetUserPk());

        if(isDelete == 0 )
        {
            throw new CustomException(ErrorCode.DELETE_INVITE_HISTORY_NOT_FOUND);
        }



    }


}
