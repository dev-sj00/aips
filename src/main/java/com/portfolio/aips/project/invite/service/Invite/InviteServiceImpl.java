package com.portfolio.aips.project.invite.service.Invite;

import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;
import com.portfolio.aips.project.invite.entity.InviteUserListEntity;
import com.portfolio.aips.project.invite.repo.InviteSearchUserRepository;
import com.portfolio.aips.project.invite.repo.InviteUserListRepository;
import com.portfolio.aips.project.users.entity.UsersEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InviteServiceImpl implements InviteService {
    private final InviteUserListRepository inviteUserListRepository;
    private final InviteSearchUserRepository inviteSearchUserRepository;

    @Override
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
    public UsersEntity findInviteUserInfo(String userName) {
        return inviteSearchUserRepository.findByNicknameStartingWith(userName)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));


    }
}
