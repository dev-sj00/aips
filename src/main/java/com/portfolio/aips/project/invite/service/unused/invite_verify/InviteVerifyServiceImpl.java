package com.portfolio.aips.project.invite.service.unused.invite_verify;

import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;
import com.portfolio.aips.project.invite.enums.InviteType;
import com.portfolio.aips.project.invite.service.unused.invite_verify.dto.MaxVerifyCommand;
import com.portfolio.aips.project.invite.service.unused.invite_verify.dto.MaxVerifyResult;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
@Deprecated
public class InviteVerifyServiceImpl implements InviteVerifyService {
    private final JPAQueryFactory queryFactory;

    @Override
    public MaxVerifyResult maxVerify(MaxVerifyCommand command) {

         EntityPathBase<?> qEntity= command.qEntity();
         NumberPath<Long> qEntityOwnerUserPk = command.ownerUserPkPath();
         EntityPathBase<?> qPolicy = command.qPolicy();
         NumberPath<Long> qPolicyPk = command.qPolicyPk();
         NumberPath<Integer> maxCountPath = command.maxCountPath();
         EnumPath<InviteType> targetTypePath = command.targetTypePath();



        Long currentCount = Optional.ofNullable(
                queryFactory.select(qEntity.count())
                        .from(command.qEntity())
                        .where(qEntityOwnerUserPk.eq(command.ownerUserPkValue()))
                        .fetchOne()
        ).orElse(0L);


        Tuple invitePolicyResult = queryFactory
                .select(qPolicyPk, maxCountPath)
                .from(qPolicy)
                .where(targetTypePath.eq(command.targetTypeValue()))
                .fetchOne();

        if(invitePolicyResult == null){
            log.info("addFavoriteFriend: invitePolicyResult is null");
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        Long maxFavInviteCount = Optional.ofNullable(invitePolicyResult.get(maxCountPath))
                .map(Integer::longValue) // Integer → Long
                .orElse(0L);

        long invitePolicyPk = Optional.ofNullable(invitePolicyResult.get(qPolicyPk)).orElse(0L);



        if(currentCount > maxFavInviteCount){
            return new MaxVerifyResult(invitePolicyPk, true);
        }

        return new MaxVerifyResult(invitePolicyPk, false);


    }

}
