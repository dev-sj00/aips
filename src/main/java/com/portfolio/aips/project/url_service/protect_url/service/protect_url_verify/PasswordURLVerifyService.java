package com.portfolio.aips.project.url_service.protect_url.service.protect_url_verify;

import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;
import com.portfolio.aips.project.url_service.protect_url.dto.command.PasswordVerifyCommand;
import com.portfolio.aips.project.url_service.protect_url.entity.ProtectURLEntity;
import com.portfolio.aips.project.url_service.protect_url.repo.ProtectURLRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordURLVerifyService implements ProtectURLVerifyService<PasswordVerifyCommand>{

    private final ProtectURLRepository protectURLRepository;
    @Override
    public ProtectURLEntity verify(PasswordVerifyCommand commandDTO) {


        return protectURLRepository
                .findByPkAndUrlPassword(commandDTO.protectUrlPk(), commandDTO.urlPassword())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_URL_PASSWORD));
    }
}
