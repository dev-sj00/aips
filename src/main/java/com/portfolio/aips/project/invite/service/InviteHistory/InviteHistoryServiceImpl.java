package com.portfolio.aips.project.invite.service.InviteHistory;

import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;

import com.portfolio.aips.project.invite.entity.InvitePolicyEntity;
import com.portfolio.aips.project.invite.entity.InviteHistoryEntity;
import com.portfolio.aips.project.invite.entity.QInviteHistoryEntity;
import com.portfolio.aips.project.invite.enums.InviteType;
import com.portfolio.aips.project.invite.repo.InviteHistoryRepository;
import com.portfolio.aips.project.invite.service.InviteHistory.command.AddInviteHistoryAndTrimOldestCommand;
import com.portfolio.aips.project.invite.service.InviteHistory.command.DeleteHistoryCommand;
import com.portfolio.aips.project.invite.service.invitePolicy.InvitePolicyService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InviteHistoryServiceImpl implements InviteHistoryService {

    private final InvitePolicyService invitePolicyService;
    private final InviteHistoryRepository inviteHistoryRepository;
    private final JPAQueryFactory queryFactory;


    @Override
    public List<InviteHistoryEntity> findAllHistory(long ownerUserPk, InviteType targetType) {
        QInviteHistoryEntity qHistory = QInviteHistoryEntity.inviteHistoryEntity;
        InvitePolicyEntity qPolicy = invitePolicyService.findInvitePolicyByInviteType(InviteType.Protect);


        return Optional
                .ofNullable(queryFactory.selectFrom(qHistory)
                .where(qHistory.invitePolicyPk.eq(qPolicy.getPk())).fetch()).orElseGet(List::of);
    }


    @Transactional
    public void deleteHistory(DeleteHistoryCommand command) {

        QInviteHistoryEntity inviteHistory = QInviteHistoryEntity.inviteHistoryEntity;

        InvitePolicyEntity invitePolicy = invitePolicyService.findInvitePolicyByInviteType(InviteType.Protect);

        long isDelete = queryFactory.delete(inviteHistory)
                .where(inviteHistory.ownerUserPk.eq(command.ownerUserPk()),
                        inviteHistory.invitePolicyPk.eq(invitePolicy.getPk()),
                        inviteHistory.targetUserPk.eq(command.targetUserPk()))
                .execute();

        if(isDelete == 0)
        {
            log.error("조작된 요청 사용자 pk {}  삭제 요청 pk {}", command.ownerUserPk(), command.targetUserPk());
            throw new CustomException(ErrorCode.DELETE_INVITE_HISTORY_NOT_FOUND);
        }



    }

    @Override
    public void addInviteHistoryAndTrimOldest(AddInviteHistoryAndTrimOldestCommand command) {
        // 1. InvitePolicy 조회

        InvitePolicyEntity invitePolicy = invitePolicyService.findInvitePolicyByInviteType(InviteType.Protect);

        QInviteHistoryEntity qHistory = QInviteHistoryEntity.inviteHistoryEntity;

        // 2. InviteHistory 추가

        inviteHistoryRepository.saveIfNotExists(command.ownerUserPk(), command.targetUserPk(), invitePolicy.getPk());
       /* InviteHistoryEntity inviteHistory = new InviteHistoryEntity();
        inviteHistory.setOwnerUserPk(command.ownerUserPk());
        inviteHistory.setTargetUserPk(command.targetUserPk());
        inviteHistory.setInvitePolicyPk(invitePolicy.getPk());
        inviteHistoryRepository.save(inviteHistory);*/

        // 3. 조건에 맞는 History 조회 (가장 오래된 순)

        List<Long> historyPkList = queryFactory.select(qHistory.pk)
                .from(qHistory)
                .where(qHistory.invitePolicyPk.eq(invitePolicy.getPk())
                        .and(qHistory.ownerUserPk.eq(command.ownerUserPk()))
                )
                .orderBy(qHistory.createdAt.asc())
                .fetch();

        // 4. 최대 개수 초과 시 가장 오래된 row 삭제
        if (historyPkList.size() > invitePolicy.getMaxHistoryCount()) {
            queryFactory.delete(qHistory)
                    .where(qHistory.pk.eq(historyPkList.get(0)))
                    .execute();
        }
    }


}
