package com.portfolio.aips.project.invite.repo;

import com.portfolio.aips.project.invite.entity.FavoriteInviteFriendEntity;
import com.portfolio.aips.project.invite.enums.InviteType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FavoriteInviteFriendRepository extends JpaRepository<FavoriteInviteFriendEntity,Long> {


}
