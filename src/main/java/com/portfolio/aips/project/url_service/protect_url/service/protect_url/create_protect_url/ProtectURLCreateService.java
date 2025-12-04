package com.portfolio.aips.project.url_service.protect_url.service.protect_url.create_protect_url;

import com.portfolio.aips.project.url_service.protect_url.dto.request.BaseCreateProtectURLRequest;
import com.portfolio.aips.project.url_service.protect_url.dto.request.InvitedUserCreateRequest;
import com.portfolio.aips.project.url_service.protect_url.entity.ProtectURLEntity;
import com.portfolio.aips.project.users.dto.CustomUserDetails;

public abstract class ProtectURLCreateService<T extends BaseCreateProtectURLRequest> {

    abstract String createProtectUrlArchive(T createProtectURLRequest, CustomUserDetails customUserDetails);
    abstract protected void updateUrlStatus(T request, ProtectURLEntity protectURLEntity);
    abstract protected ProtectURLEntity createProtectURLEntity(T createProtectURLRequest, CustomUserDetails customUserDetails);
}
