package com.portfolio.aips.project.invite.service.FavoriteInviteFriend;

import com.portfolio.aips.project.invite.service.FavoriteInviteFriend.command.AddFavoriteFriendCommand;
import com.portfolio.aips.project.invite.service.FavoriteInviteFriend.command.DeleteFavoriteFriendCommand;
import com.portfolio.aips.project.invite.entity.FavoriteInviteFriendEntity;
import com.portfolio.aips.project.invite.enums.InviteType;

import java.util.List;
import java.util.Set;

public interface FavoriteInviteFriendService {
    void addFavoriteFriend(AddFavoriteFriendCommand command);
    Set<FavoriteInviteFriendEntity> findAllFavoriteFriends(long ownerUserPk, InviteType targetType);
    void deleteFavoriteFriend(DeleteFavoriteFriendCommand command);
}
