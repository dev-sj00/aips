package com.portfolio.aips.project.interaction.view.repo.dto.result;

import lombok.Getter;

import java.util.List;


public record FindByHbKeyResult(Long size, List<Object> times) {
}
