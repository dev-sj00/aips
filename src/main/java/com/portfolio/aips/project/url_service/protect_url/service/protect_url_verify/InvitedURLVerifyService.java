package com.portfolio.aips.project.url_service.protect_url.service.protect_url_verify;

import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;
import com.portfolio.aips.project.url_service.protect_url.dto.command.InvitedUserVerifyCommand;
import com.portfolio.aips.project.url_service.protect_url.entity.ProtectURLEntity;
import com.portfolio.aips.project.url_service.protect_url.repo.ProtectURLRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class InvitedURLVerifyService implements ProtectURLVerifyService<InvitedUserVerifyCommand>{

    private final ProtectURLRepository protectURLRepository;

    @Override
    public ProtectURLEntity verify(InvitedUserVerifyCommand commandDTO) {

        return protectURLRepository.findWithInvitedUser(commandDTO.protectUrlPk(), commandDTO.userPk())
                .orElseThrow(() -> new CustomException(ErrorCode.URL_FORBIDDEN_USER));
    }
}
