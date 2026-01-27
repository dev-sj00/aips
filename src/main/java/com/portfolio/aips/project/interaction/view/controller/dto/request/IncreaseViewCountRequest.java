package com.portfolio.aips.project.interaction.view.controller.dto.request;


import com.portfolio.aips.project.interaction.common.enums.BoardType;
import jakarta.validation.constraints.NotBlank;

public record IncreaseViewCountRequest(@NotBlank long boardPk, @NotBlank BoardType boardType) {

}
