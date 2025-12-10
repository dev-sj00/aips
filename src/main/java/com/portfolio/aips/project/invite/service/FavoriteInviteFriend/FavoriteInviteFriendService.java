package com.portfolio.aips.project.invite.service.FavoriteInviteFriend;

import com.portfolio.aips.project.invite.service.FavoriteInviteFriend.command.AddFavoriteFriendCommand;
import com.portfolio.aips.project.invite.service.FavoriteInviteFriend.command.DeleteFavoriteFriendCommand;
import com.portfolio.aips.project.invite.entity.FavoriteInviteFriendEntity;
import com.portfolio.aips.project.invite.enums.InviteType;

import java.util.List;

public interface FavoriteInviteFriendService {
    void addFavoriteFriend(AddFavoriteFriendCommand command);
    List<FavoriteInviteFriendEntity> findAllFavoriteFriends(long ownerUserPk, InviteType targetType);
    void deleteFavoriteFriend(DeleteFavoriteFriendCommand command);
}
