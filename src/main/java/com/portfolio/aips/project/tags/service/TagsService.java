package com.portfolio.aips.project.tags.service;

import com.portfolio.aips.project.tags.dto.CreateTagsCommand;

import java.util.List;

public interface TagsService {
    void createTagProc(CreateTagsCommand createTagsCommand);
}
