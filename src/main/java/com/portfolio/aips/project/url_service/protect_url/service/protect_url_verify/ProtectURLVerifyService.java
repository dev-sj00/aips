package com.portfolio.aips.project.url_service.protect_url.service.protect_url_verify;

import com.portfolio.aips.project.url_service.protect_url.entity.ProtectURLEntity;

public interface ProtectURLVerifyService<T> {
    ProtectURLEntity verify(T commandDTO);
}
