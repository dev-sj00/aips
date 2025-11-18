package com.portfolio.aips.project.tags.dto;


import java.util.List;

public record CreateTagsCommand(Long boardId, String boardType, List<String> tagNames) {
}
