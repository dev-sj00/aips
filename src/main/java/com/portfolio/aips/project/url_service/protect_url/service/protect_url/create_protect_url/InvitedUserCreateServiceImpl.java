package com.portfolio.aips.project.url_service.protect_url.service.protect_url.create_protect_url;

import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;
import com.portfolio.aips.project.url_service.archive.dto.request.CreateArchiveRequest;
import com.portfolio.aips.project.url_service.archive.entity.ArchiveEntity;
import com.portfolio.aips.project.url_service.common.entity.URLStatusEntity;
import com.portfolio.aips.project.url_service.common.repo.URLStatusRepository;
import com.portfolio.aips.project.url_service.common.service.url_generator.URLGeneratorService;
import com.portfolio.aips.project.url_service.common.service.url_generator.enums.URLGeneratorType;
import com.portfolio.aips.project.url_service.protect_url.dto.request.InvitedUserCreateRequest;
import com.portfolio.aips.project.url_service.protect_url.entity.ProtectURLEntity;
import com.portfolio.aips.project.url_service.protect_url.entity.ProtectURLInvitedEntity;
import com.portfolio.aips.project.url_service.protect_url.repo.ProtectURLInvitedRepository;
import com.portfolio.aips.project.url_service.protect_url.repo.ProtectURLRepository;
import com.portfolio.aips.project.users.dto.CustomUserDetails;
import com.portfolio.aips.project.utils.enums.LLMType;
import com.portfolio.aips.project.utils.enums.LLMUrlPrefix;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("InvitedUserCreateService")
@RequiredArgsConstructor

public class InvitedUserCreateServiceImpl extends ProtectURLCreateService<InvitedUserCreateRequest> {
    private final ProtectURLRepository protectURLRepository;
    private final ProtectURLInvitedRepository protectURLInvitedRepository;
    private final URLGeneratorService  urlGeneratorService;
    private final URLStatusRepository urlStatusRepository;


    @Override
    @Transactional
    public String createProtectUrlArchive(InvitedUserCreateRequest createProtectURLRequest, CustomUserDetails customUserDetails) {
        ProtectURLEntity protectURLEntity = createProtectURLEntity(createProtectURLRequest, customUserDetails);
        protectURLRepository.save(protectURLEntity);

        List<ProtectURLInvitedEntity> protectURLInvitedEntities = new ArrayList<>();

        for(long invitedUserPk : createProtectURLRequest.getInvitedUserPkList())
        {
            ProtectURLInvitedEntity protectURLInvitedEntity = new ProtectURLInvitedEntity();
            protectURLInvitedEntity.setInvitedUserPk(invitedUserPk);
            protectURLInvitedEntity.setProtectUrlPk(protectURLEntity.getPk());

            protectURLInvitedEntities.add(protectURLInvitedEntity);

        }
        protectURLInvitedRepository.saveAll(protectURLInvitedEntities);

        return protectURLEntity.getSiteSlug();
    }

    protected void updateUrlStatus(InvitedUserCreateRequest request, ProtectURLEntity protectURLEntity) {
        URLStatusEntity urlStatusEntity = urlStatusRepository
                .findByIsCreatedAndUrlLink(false, request.getUrlLink())
                .orElseThrow(() -> new CustomException(ErrorCode.URL_NOT_FOUND));

        urlStatusEntity.setCreated(true);

        protectURLEntity.setUrlStatusEntity(urlStatusEntity);
    }


    protected ProtectURLEntity createProtectURLEntity(InvitedUserCreateRequest createProtectURLRequest, CustomUserDetails customUserDetails)
    {

        ProtectURLEntity protectURLEntity = new ProtectURLEntity();

        protectURLEntity.setWriterPk(customUserDetails.getPk());
        LLMType llmType = LLMType.valueOf(LLMUrlPrefix.findKeyByUrl(createProtectURLRequest.getUrlLink()));
        protectURLEntity.setLlmType(llmType);
        String siteSlug = urlGeneratorService.createUrlProc(customUserDetails.getPk(), URLGeneratorType.Protector);
        protectURLEntity.setSiteSlug(siteSlug);
        protectURLEntity.setDescription(createProtectURLRequest.getDescription());
        protectURLEntity.setTitle(createProtectURLRequest.getTitle());

        return protectURLEntity;

    }


}
