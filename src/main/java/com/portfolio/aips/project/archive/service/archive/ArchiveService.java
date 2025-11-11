package com.portfolio.aips.project.archive.service.archive;

import com.portfolio.aips.project.archive.dto.request.CreateArchiveRequest;
import com.portfolio.aips.project.users.dto.CustomUserDetails;

public interface ArchiveService {
    void createArchive(CreateArchiveRequest createArchiveRequest, CustomUserDetails customUserDetails);

}
