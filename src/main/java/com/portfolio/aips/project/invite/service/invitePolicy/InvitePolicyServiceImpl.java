package com.portfolio.aips.project.invite.service.invitePolicy;

import com.portfolio.aips.project.invite.entity.InvitePolicyEntity;
import com.portfolio.aips.project.invite.entity.QInvitePolicyEntity;
import com.portfolio.aips.project.invite.enums.InviteType;
import com.portfolio.aips.project.invite.repo.InvitePolicyRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InvitePolicyServiceImpl implements InvitePolicyService{

   private final JPAQueryFactory queryFactory;
   private final InvitePolicyRepository invitePolicyRepository;
    @Override
    @Cacheable(value = "invitePolicyCache", key = "#inviteType")
    public InvitePolicyEntity findInvitePolicyByInviteType(InviteType inviteType) {
        QInvitePolicyEntity qPolicy = QInvitePolicyEntity.invitePolicyEntity;

        return  Optional.ofNullable(
                queryFactory.selectFrom(qPolicy)
                        .where(qPolicy.targetType.eq(InviteType.Protect))
                        .fetchOne()
        ).orElseGet(() -> {
            // 없으면 새로 생성
            InvitePolicyEntity newPolicy = new InvitePolicyEntity();
            newPolicy.setTargetType(InviteType.Protect);
            newPolicy.setMaxFavoriteCount(50); // 기본 최대 개수 설정
            newPolicy.setMaxHistoryCount(5);
            newPolicy.setMaxInviteCount(20);
            return invitePolicyRepository.saveAndFlush(newPolicy);
        });
    }
}
