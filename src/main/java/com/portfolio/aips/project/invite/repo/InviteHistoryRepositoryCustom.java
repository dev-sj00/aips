package com.portfolio.aips.project.invite.repo;

public interface InviteHistoryRepositoryCustom {
    void saveIfNotExists(Long ownerUserPk, Long targetUserPk, Long invitePolicyPk);
}
