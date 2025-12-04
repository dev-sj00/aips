package com.portfolio.aips.project.url_service.protect_url.dto.command;

public record PasswordVerifyCommand(String urlPassword, long protectUrlPk) {
}
