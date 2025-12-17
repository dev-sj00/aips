package com.portfolio.aips.project.invite.repo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class InviteHistoryRepositoryCustomImpl implements InviteHistoryRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    @Override
    public void saveIfNotExists(Long ownerUserPk, Long targetUserPk, Long invitePolicyPk) {
        //포팅 필요 시 merge
        //creationTimeStamp 안통함

        String sql = "INSERT INTO invite_history (owner_user_pk, target_user_pk, invite_policy_pk, created_date_time) " +
                "VALUES (:ownerUserPk, :targetUserPk, :invitePolicyPk, now())" +
                "ON CONFLICT (owner_user_pk, target_user_pk, invite_policy_pk) DO NOTHING";

        em.createNativeQuery(sql)
                .setParameter("ownerUserPk", ownerUserPk)
                .setParameter("targetUserPk", targetUserPk)
                .setParameter("invitePolicyPk", invitePolicyPk)
                .executeUpdate();




    }
}
