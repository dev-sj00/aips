package com.portfolio.aips.project.interaction.report.app.service.command;

import com.portfolio.aips.project.interaction.report.domain.model.BanType;

public record CreateActiveSanctionsCommand(Long sanctionPk, BanType banType) {
}
