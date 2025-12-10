package com.portfolio.aips.project.invite.service.FavoriteInviteFriend;

import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;
import com.portfolio.aips.project.invite.service.FavoriteInviteFriend.command.AddFavoriteFriendCommand;
import com.portfolio.aips.project.invite.service.FavoriteInviteFriend.command.DeleteFavoriteFriendCommand;
import com.portfolio.aips.project.invite.entity.FavoriteInviteFriendEntity;
import com.portfolio.aips.project.invite.entity.InviteEntity;
import com.portfolio.aips.project.invite.enums.InviteType;
import com.portfolio.aips.project.invite.repo.FavoriteInviteFriendRepository;
import com.portfolio.aips.project.invite.repo.InviteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteInviteFriendServiceImpl implements FavoriteInviteFriendService{

    private final InviteRepository inviteRepository;
    private final FavoriteInviteFriendRepository favoriteInviteFriendRepository;

    @Override
    @Transactional
    public void addFavoriteFriend(AddFavoriteFriendCommand command) {
        InviteEntity inviteEntity = inviteRepository.findByOwnerUserPkAndTargetType(command.ownerUserPk(), command.targetType())
                .orElseThrow(() ->
                {
                    log.error("pk : {} targetType : {} NOT FOUND InviteEntity ", command.ownerUserPk(), command.targetType());
                    return new CustomException(ErrorCode.INTERNAL_SERVER_ERROR); // 사용자 잘못이 아님
                });

        int currentFavInviteCount = favoriteInviteFriendRepository.countByInvitePk(inviteEntity.getPk());
        int maxFavInviteCount = inviteEntity.getMaxInviteCount();

        if(currentFavInviteCount >= maxFavInviteCount){
            throw new CustomException(ErrorCode.MAX_FAVORITE_FRIEND_REACHED);
        }


        FavoriteInviteFriendEntity favoriteInviteFriendEntity = new FavoriteInviteFriendEntity();
        favoriteInviteFriendEntity.setInvitePk(inviteEntity.getPk());
        favoriteInviteFriendEntity.setUserPk(command.ownerUserPk());
        inviteEntity.addFavoriteInviteFriend(favoriteInviteFriendEntity);



        
        

    }

    @Override
    public List<FavoriteInviteFriendEntity> findAllFavoriteFriends(long ownerUserPk, InviteType targetType) {

        InviteEntity inviteEntity = inviteRepository.findWithFavoritesByOwnerUserPkAndTargetType(ownerUserPk, targetType)
                .orElseThrow(() ->
                {
                    log.error("pk : {} targetType : {} NOT FOUND InviteEntity ", ownerUserPk, targetType);
                    return new CustomException(ErrorCode.INTERNAL_SERVER_ERROR); // 사용자 잘못이 아님
                });

        return inviteEntity.getFavoriteInviteFriends();
    }

    @Transactional
    @Override
    public void deleteFavoriteFriend(DeleteFavoriteFriendCommand command) {
        InviteEntity inviteEntity = inviteRepository.findByOwnerUserPkAndTargetType(command.ownerUserPk(), command.targetType())
                .orElseThrow(() ->
                {
                    log.error("pk : {} targetType : {} NOT FOUND InviteEntity ", command.ownerUserPk(), command.targetType());
                    return new CustomException(ErrorCode.INTERNAL_SERVER_ERROR); // 사용자 잘못이 아님
                });

        favoriteInviteFriendRepository.deleteByInvitePkAndUserPk(inviteEntity.getPk(), command.targetUserPk());
    }

}
