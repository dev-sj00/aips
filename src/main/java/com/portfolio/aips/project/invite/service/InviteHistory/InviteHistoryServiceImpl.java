package com.portfolio.aips.project.invite.service.InviteHistory;

import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;
import com.portfolio.aips.project.invite.dto.command.AddHistoryCommand;
import com.portfolio.aips.project.invite.entity.InviteEntity;
import com.portfolio.aips.project.invite.entity.InviteHistoryEntity;
import com.portfolio.aips.project.invite.enums.InviteType;
import com.portfolio.aips.project.invite.repo.InviteHistoryRepository;
import com.portfolio.aips.project.invite.repo.InviteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InviteHistoryServiceImpl implements InviteHistoryService {

    private final InviteRepository inviteRepository;
    private final InviteHistoryRepository inviteHistoryRepository;


    @Override
    public List<InviteHistoryEntity> findAllHistory(long ownerUserPk, InviteType targetType) {
        InviteEntity inviteEntity = inviteRepository.findWithFavoritesByOwnerUserPkAndTargetType(ownerUserPk, targetType)
                .orElseThrow(() ->
                {
                    log.error("pk : {} targetType : {} NOT FOUND InviteEntity ", ownerUserPk, targetType);
                    return new CustomException(ErrorCode.INTERNAL_SERVER_ERROR); // 사용자 잘못이 아님
                });

        return inviteEntity.getInviteHistory();
    }

    @Override
    public void addHistory(AddHistoryCommand command) { //inviteUser 검색 후 위 서비스 실행
        InviteHistoryEntity inviteHistoryEntity = new InviteHistoryEntity();
        inviteHistoryEntity.setInvitePk(command.invitePk());
        inviteHistoryEntity.setUserPk(command.targetUserPk());
        inviteHistoryRepository.save(inviteHistoryEntity);
    }

    @Override
    public void deleteHistory(long invitePk, long targetUserPk) {
        inviteHistoryRepository.deleteByInvitePkAndUserPk(invitePk, targetUserPk);
    }
}
