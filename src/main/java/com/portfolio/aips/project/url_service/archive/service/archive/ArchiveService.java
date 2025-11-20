package com.portfolio.aips.project.url_service.archive.service.archive;

import com.portfolio.aips.project.url_service.archive.dto.request.CreateArchiveRequest;
import com.portfolio.aips.project.url_service.common.dto.request.VerifyRequest;
import com.portfolio.aips.project.users.dto.CustomUserDetails;

public interface ArchiveService {
    long createArchive(CreateArchiveRequest createArchiveRequest, CustomUserDetails customUserDetails);

}
