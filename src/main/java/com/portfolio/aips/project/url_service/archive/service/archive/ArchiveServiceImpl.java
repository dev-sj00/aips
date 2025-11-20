package com.portfolio.aips.project.url_service.archive.service.archive;

import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;
import com.portfolio.aips.project.url_service.archive.dto.request.CreateArchiveRequest;
import com.portfolio.aips.project.url_service.common.dto.request.VerifyRequest;
import com.portfolio.aips.project.url_service.archive.entity.ArchiveEntity;
import com.portfolio.aips.project.url_service.archive.repo.ArchiveRepository;

import com.portfolio.aips.project.url_service.common.entity.URLStatusEntity;
import com.portfolio.aips.project.url_service.common.repo.URLStatusRepository;
import com.portfolio.aips.project.url_service.common.service.url_generator.URLGeneratorService;
import com.portfolio.aips.project.url_service.common.service.url_generator.enums.URLGeneratorType;

import com.portfolio.aips.project.url_service.common.service.url_status.URLStatusService;
import com.portfolio.aips.project.users.dto.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArchiveServiceImpl implements ArchiveService {

    private final ArchiveRepository archiveRepository;
    private final URLStatusRepository urlStatusRepository;
    private final URLGeneratorService urlGeneratorService;




    @Override
    public long createArchive(CreateArchiveRequest request, CustomUserDetails customUserDetails) {
        //검증 -> 사이트
        //
        // 분리
        //검증링크 생성
        long userPk = customUserDetails.getPk();
        String newSiteSlug = urlGeneratorService.createUrlProc(userPk, URLGeneratorType.Archive);
        //fk addUser
        ArchiveEntity archiveEntity = getNewArchiveEntity(request, newSiteSlug, userPk);
        //db 저장
        archiveRepository.save(archiveEntity);

        return archiveEntity.getPk();
    }

    private ArchiveEntity getNewArchiveEntity(CreateArchiveRequest request, String siteSlug, long userPk){
        ArchiveEntity archiveEntity = new ArchiveEntity();
        archiveEntity.setTitle(request.title());
        archiveEntity.setSiteSlug(siteSlug);
        archiveEntity.setDescription(request.description());
        archiveEntity.setUserPk(userPk);



        URLStatusEntity urlStatusEntity = urlStatusRepository
                .findByIsCreatedAndUrlLink(false, request.urlLink())
                .orElseThrow(() -> new CustomException(ErrorCode.URL_NOT_FOUND));

        urlStatusEntity.setCreated(true);

        archiveEntity.setUrlStatusEntity(urlStatusEntity);

        return archiveEntity;


    }
}
