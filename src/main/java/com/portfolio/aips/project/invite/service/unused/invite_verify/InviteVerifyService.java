package com.portfolio.aips.project.invite.service.unused.invite_verify;

import com.portfolio.aips.project.invite.service.unused.invite_verify.dto.MaxVerifyCommand;
import com.portfolio.aips.project.invite.service.unused.invite_verify.dto.MaxVerifyResult;

@Deprecated
public interface InviteVerifyService {
    MaxVerifyResult maxVerify(MaxVerifyCommand command);

}
