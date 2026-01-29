package com.portfolio.aips.project.invite.service.Invite;

import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;
import com.portfolio.aips.project.invite.entity.*;
import com.portfolio.aips.project.invite.repo.InviteSearchUserRepository;
import com.portfolio.aips.project.invite.repo.InviteUserListRepository;
import com.portfolio.aips.project.invite.service.Invite.command.FindInviteUserInfoCommand;
import com.portfolio.aips.project.invite.service.Invite.command.SaveAllCommand;
import com.portfolio.aips.project.invite.service.InviteHistory.InviteHistoryService;
import com.portfolio.aips.project.invite.service.InviteHistory.command.AddInviteHistoryAndTrimOldestCommand;
import com.portfolio.aips.project.users.entity.UsersEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class InviteServiceImpl implements InviteService {
    private final InviteUserListRepository inviteUserListRepository;
    private final InviteSearchUserRepository inviteSearchUserRepository;
    private final InviteHistoryService inviteHistoryService;


    @Override
    @Transactional
    public void saveAll(SaveAllCommand command ) {
        List<InviteUserListEntity> inviteUserListEntities = new ArrayList<>();


        for(long invitedUserPk : command.targetUserPkList())
        {
            InviteUserListEntity inviteUserListEntity = new InviteUserListEntity();
            inviteUserListEntity.setInvitePolicyPk(command.invitePolicyPk());
            inviteUserListEntity.setOwnerUserPk(command.ownerUserPk());
            inviteUserListEntity.setTargetUserPk(invitedUserPk);
            inviteUserListEntities.add(inviteUserListEntity);

        }



        inviteUserListRepository.saveAll(inviteUserListEntities);
    }

    @Override
    @Transactional
    //본인 pk값이 targetPk가 되면안됨 controller에서 throw 처리
    public UsersEntity findInviteUserInfoProc(FindInviteUserInfoCommand command) {

        UsersEntity usersEntity = inviteSearchUserRepository.findByNickname(command.targetUserName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));


        inviteHistoryService.addInviteHistoryAndTrimOldest(convertToCommand(command, usersEntity.getPk()));


        return usersEntity;

    }

    private AddInviteHistoryAndTrimOldestCommand convertToCommand(FindInviteUserInfoCommand command, long targetUserPk) {
        return new AddInviteHistoryAndTrimOldestCommand(command.ownerUserPk(), targetUserPk);
    }



    }

