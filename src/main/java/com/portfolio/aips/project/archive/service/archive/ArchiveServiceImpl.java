package com.portfolio.aips.project.archive.service.archive;

import com.portfolio.aips.project.archive.dto.request.CreateArchiveRequest;
import com.portfolio.aips.project.archive.entity.ArchiveEntity;
import com.portfolio.aips.project.archive.repo.ArchiveRepository;
import com.portfolio.aips.project.archive.service.UrlValidator.UrlValidatorService;
import com.portfolio.aips.project.archive.service.UrlValidator.enums.URLValidatorServiceImplName;
import com.portfolio.aips.project.archive.service.url_generator.URLGeneratorService;
import com.portfolio.aips.project.archive.service.url_generator.enums.URLGeneratorType;
import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;
import com.portfolio.aips.project.users.dto.CustomUserDetails;
import com.portfolio.aips.project.users.entity.UsersEntity;
import com.portfolio.aips.project.users.repo.UsersRepository;
import com.portfolio.aips.project.utils.UrlValidatorUtils;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArchiveServiceImpl implements ArchiveService {

    private final ArchiveRepository archiveRepository;
    private final URLGeneratorService urlGeneratorService;




    @Override
    public void createArchive(CreateArchiveRequest createArchiveRequest, CustomUserDetails customUserDetails) {
        //검증 -> 사이트
        //

        // 분리
        //검증링크 생성
        long userPk = customUserDetails.getPk();
        String newSiteSlug = urlGeneratorService.createUrlProc(userPk, URLGeneratorType.Archive);
        //fk addUser
        ArchiveEntity archiveEntity = getNewArchiveEntity(createArchiveRequest, newSiteSlug, userPk);
        //db 저장
        archiveRepository.save(archiveEntity);
    }

    private ArchiveEntity getNewArchiveEntity(CreateArchiveRequest request, String siteSlug, long userPk){
        ArchiveEntity archiveEntity = new ArchiveEntity();
        archiveEntity.setArchiveLink(request.archiveLink());
        archiveEntity.setTitle(request.title());
        archiveEntity.setSiteAlive(true);
        archiveEntity.setSiteSlug(siteSlug);

        archiveEntity.setUserPk(userPk);

        return archiveEntity;


    }
}
