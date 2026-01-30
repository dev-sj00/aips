package com.portfolio.aips.project.interaction.sanction.app.service.active_sanction.command;

import com.portfolio.aips.project.interaction.report.domain.model.BanType;

public record UpdateActiveSanctionCommand(BanType banType, Long targetUserPk) {
}
