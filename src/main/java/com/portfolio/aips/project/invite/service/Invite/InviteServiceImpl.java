package com.portfolio.aips.project.invite.service.Invite;

import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;
import com.portfolio.aips.project.invite.entity.InviteEntity;
import com.portfolio.aips.project.invite.entity.InviteHistoryEntity;
import com.portfolio.aips.project.invite.entity.InviteUserListEntity;
import com.portfolio.aips.project.invite.enums.InviteType;
import com.portfolio.aips.project.invite.repo.InviteHistoryRepository;
import com.portfolio.aips.project.invite.repo.InviteRepository;
import com.portfolio.aips.project.invite.repo.InviteSearchUserRepository;
import com.portfolio.aips.project.invite.repo.InviteUserListRepository;
import com.portfolio.aips.project.invite.service.Invite.command.FindInviteUserInfoCommand;
import com.portfolio.aips.project.users.entity.UsersEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class InviteServiceImpl implements InviteService {
    private final InviteUserListRepository inviteUserListRepository;
    private final InviteSearchUserRepository inviteSearchUserRepository;
    private final InviteHistoryRepository inviteHistoryRepository;
    private final InviteRepository inviteRepository;

    @Override
    @Transactional
    public void saveAll(long invitePk, List<Long> invitedUserPkList) {
        List<InviteUserListEntity> inviteUserListEntities = new ArrayList<>();
        for(long invitedUserPk : invitedUserPkList)
        {
            InviteUserListEntity inviteUserListEntity = new InviteUserListEntity();
            inviteUserListEntity.setUserPk(invitedUserPk);
            inviteUserListEntity.setInvitePk(invitePk);
            inviteUserListEntities.add(inviteUserListEntity);

        }



        inviteUserListRepository.saveAll(inviteUserListEntities);
    }

    @Override
    @Transactional
    public UsersEntity findInviteUserInfoProc(FindInviteUserInfoCommand command) {

        UsersEntity usersEntity = inviteSearchUserRepository.findByNickname(command.targetUserName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));


        removeOldestInviteHistory(command, usersEntity.getPk());


        return usersEntity;

    }

    private void removeOldestInviteHistory(FindInviteUserInfoCommand command, long targetUserPk) {
        InviteEntity inviteEntity = initializeInviteEntity(command.ownerUserPk(), command.inviteType());





        InviteHistoryEntity inviteHistoryEntity = new InviteHistoryEntity();
        inviteHistoryEntity.setUserPk(targetUserPk);
        inviteEntity.addInviteHistory(inviteHistoryEntity);

        if(inviteEntity.getInviteHistory().size() > inviteEntity.getMaxHistoryCount())
        {
            log.info("remove oldest invite history");

            List<InviteHistoryEntity> sortedHistory = inviteEntity.getInviteHistory()
                    .stream()
                    .sorted(Comparator.comparing(
                            InviteHistoryEntity::getCreatedAt,
                            Comparator.nullsLast(Comparator.naturalOrder()) // null이면 마지막
                    ))
                    .toList();

            InviteHistoryEntity oldestEntity = sortedHistory.get(0);
            inviteEntity.getInviteHistory().remove(oldestEntity);
            log.info("remove oldest invite history {}", oldestEntity.getPk()   );

        }


    }


    private InviteEntity initializeInviteEntity(long ownerUserPk, InviteType inviteType) {

        if (Objects.requireNonNull(inviteType) == InviteType.Protect) {
            return inviteRepository
                    .findWithHistoryByOwnerUserPkAndTargetType(ownerUserPk, inviteType)
                    .orElseGet(() -> {
                        InviteEntity newInvite = new InviteEntity();
                        newInvite.setOwnerUserPk(ownerUserPk); // FK 값 세팅
                        newInvite.setTargetType(inviteType);
                        newInvite.setMaxInviteCount(20); // 필요 시 기본값 세팅
                        newInvite.setMaxFavoriteCount(30);
                        newInvite.setMaxHistoryCount(5);
                        return inviteRepository.save(newInvite); // DB에 저장
                    });
        }

        log.error("잘못된 형식에 InviteType 입니다.");
        throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
    }





}
