package com.portfolio.aips.project.url_service.protect_url.service.protect_url;

import com.portfolio.aips.project.url_service.protect_url.repo.ProtectURLRepository;
import com.portfolio.aips.project.url_service.protect_url.service.protect_url.create_protect_url.ProtectURLServiceCreateMethod;
import com.portfolio.aips.project.url_service.protect_url.service.protect_url.create_protect_url.ProtectURLCreateService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service

public class ProtectURLServiceServiceImpl extends ProtectURLServiceCreateMethod implements ProtectURLService {

    private final ProtectURLRepository protectURLRepository;


    public ProtectURLServiceServiceImpl(Map<String, ProtectURLCreateService<?>> protectURLCreateServices, ProtectURLRepository protectURLRepository) {
        super(protectURLCreateServices);
        this.protectURLRepository = protectURLRepository;
    }
}
