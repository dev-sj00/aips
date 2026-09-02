package com.portfolio.aips.project.invite.service.FavoriteInviteFriend;

import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;
import com.portfolio.aips.project.invite.entity.QFavoriteInviteFriendEntity;
import com.portfolio.aips.project.invite.entity.QInvitePolicyEntity;
import com.portfolio.aips.project.invite.service.FavoriteInviteFriend.command.AddFavoriteFriendCommand;
import com.portfolio.aips.project.invite.service.FavoriteInviteFriend.command.DeleteFavoriteFriendCommand;
import com.portfolio.aips.project.invite.entity.FavoriteInviteFriendEntity;
import com.portfolio.aips.project.invite.entity.InvitePolicyEntity;
import com.portfolio.aips.project.invite.enums.InviteType;
import com.portfolio.aips.project.invite.repo.FavoriteInviteFriendRepository;
import com.portfolio.aips.project.invite.repo.InvitePolicyRepository;
import com.querydsl.core.QueryFactory;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteInviteFriendServiceImpl implements FavoriteInviteFriendService{

    private final FavoriteInviteFriendRepository favoriteInviteFriendRepository;
    private final JPAQueryFactory queryFactory;



    @Override
    @Transactional
    //본인 pk값이 targetPk가 되면안됨 controller에서 throw 처리
    public void addFavoriteFriend(AddFavoriteFriendCommand command) {
        try {
            QFavoriteInviteFriendEntity qFavFriend = QFavoriteInviteFriendEntity.favoriteInviteFriendEntity;

            Long currentFavInviteCount = Optional.ofNullable(
                    queryFactory.select(qFavFriend.count())
                            .from(qFavFriend)
                            .where(qFavFriend.ownerUserPk.eq(command.ownerUserPk()))
                            .fetchOne()
            ).orElse(0L);


            QInvitePolicyEntity qPolicy = QInvitePolicyEntity.invitePolicyEntity;
            Tuple invitePolicyResult = queryFactory
                    .select(qPolicy.pk, qPolicy.maxFavoriteCount)
                    .from(qPolicy)
                    .where(qPolicy.targetType.eq(command.targetType()))
                    .fetchOne();

            if (invitePolicyResult == null) {
                log.info("addFavoriteFriend: invitePolicyResult is null");
                throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
            }

            Long maxFavInviteCount = Optional.ofNullable(invitePolicyResult.get(qPolicy.maxFavoriteCount))
                    .map(Integer::longValue) // Integer → Long
                    .orElse(0L);

            Long invitePolicyPk = Optional.ofNullable(invitePolicyResult.get(qPolicy.pk)).orElse(0L);


            if (currentFavInviteCount >= maxFavInviteCount) {
                throw new CustomException(ErrorCode.MAX_FAVORITE_FRIEND_REACHED);
            }


            FavoriteInviteFriendEntity favoriteInviteFriendEntity = new FavoriteInviteFriendEntity();
            favoriteInviteFriendEntity.setOwnerUserPk(command.ownerUserPk());
            favoriteInviteFriendEntity.setInvitePolicyPk(invitePolicyPk);
            favoriteInviteFriendEntity.setTargetUserPk(command.targetUserPk());
            favoriteInviteFriendRepository.save(favoriteInviteFriendEntity);
        }catch (Exception e){
            log.error("addFavoriteFriend: ", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }


        
        

    }

    @Override
    public Set<FavoriteInviteFriendEntity> findAllFavoriteFriends(long ownerUserPk, InviteType targetType) {
        QInvitePolicyEntity qPolicy = QInvitePolicyEntity.invitePolicyEntity;
        QFavoriteInviteFriendEntity qFavFriend = QFavoriteInviteFriendEntity.favoriteInviteFriendEntity;
        Long invitePolicyPk = Optional.ofNullable(queryFactory
                .select(qPolicy.pk)
                .from(qPolicy)
                .where(qPolicy.targetType.eq(targetType))
                .fetchOne())
                .orElseThrow(() -> new CustomException(ErrorCode.INTERNAL_SERVER_ERROR));




        List<FavoriteInviteFriendEntity> list = queryFactory
                .selectFrom(qFavFriend)
                .where(qFavFriend.ownerUserPk.eq(ownerUserPk)
                        .and(qFavFriend.invitePolicyPk.eq(invitePolicyPk)))
                .fetch();

        return new HashSet<>(list);
    }

    @Transactional
    @Override
    public void deleteFavoriteFriend(DeleteFavoriteFriendCommand command) {

        QInvitePolicyEntity qPolicy = QInvitePolicyEntity.invitePolicyEntity;
        QFavoriteInviteFriendEntity qFavFriend = QFavoriteInviteFriendEntity.favoriteInviteFriendEntity;

        Long invitePolicyPk = Optional.ofNullable(queryFactory
                        .select(qPolicy.pk)
                        .from(qPolicy)
                        .where(qPolicy.targetType.eq(command.targetType()))
                        .fetchOne())
                .orElseThrow(() -> new CustomException(ErrorCode.INTERNAL_SERVER_ERROR));


        long deletedCount = queryFactory.delete(qFavFriend)
                .where(qFavFriend.invitePolicyPk.eq(invitePolicyPk)
                        .and(qFavFriend.ownerUserPk.eq(command.ownerUserPk()))
                        .and(qFavFriend.targetUserPk.eq(command.targetUserPk()))
                        .and(qFavFriend.invitePolicyPk.eq(invitePolicyPk))
                )
                .execute();

        log.info("deleteFavoriteFriend: deletedCount={}", deletedCount);

    }

}
