package com.portfolio.aips.project.interaction.view.controller.dto.request;


import com.portfolio.aips.project.interaction.enums.BoardType;
import jakarta.validation.constraints.NotBlank;
import org.springframework.lang.Nullable;

public record IncreaseViewCountRequest(@NotBlank long boardPk, @NotBlank BoardType boardType) {

}
