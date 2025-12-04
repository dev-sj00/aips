package com.portfolio.aips.project.url_service.protect_url.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;

@Getter
public class InvitedUserCreateRequest extends BaseCreateProtectURLRequest{

    @NotEmpty(message = "초대할 사용자를 최소 1명 선택해야 합니다.")
    List<Long> invitedUserPkList;

}
