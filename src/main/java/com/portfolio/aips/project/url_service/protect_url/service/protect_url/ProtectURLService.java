package com.portfolio.aips.project.url_service.protect_url.service.protect_url;

import com.portfolio.aips.project.url_service.protect_url.dto.request.BaseCreateProtectURLRequest;
import com.portfolio.aips.project.users.dto.CustomUserDetails;

public interface ProtectURLService {

    <T extends BaseCreateProtectURLRequest> String createProtectUrl(T requestDTO, CustomUserDetails customUserDetails);

}
