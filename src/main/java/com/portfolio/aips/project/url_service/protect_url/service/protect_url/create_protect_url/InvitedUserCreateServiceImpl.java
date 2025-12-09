package com.portfolio.aips.project.url_service.protect_url.service.protect_url.create_protect_url;

import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;
import com.portfolio.aips.project.invite.entity.InviteEntity;
import com.portfolio.aips.project.invite.enums.InviteType;
import com.portfolio.aips.project.invite.repo.InviteRepository;
import com.portfolio.aips.project.invite.repo.InviteUserListRepository;
import com.portfolio.aips.project.invite.service.Invite.InviteService;
import com.portfolio.aips.project.url_service.common.entity.URLStatusEntity;
import com.portfolio.aips.project.url_service.common.repo.URLStatusRepository;
import com.portfolio.aips.project.url_service.common.service.url_generator.URLGeneratorService;
import com.portfolio.aips.project.url_service.common.service.url_generator.enums.URLGeneratorType;
import com.portfolio.aips.project.url_service.protect_url.dto.request.InvitedUserCreateRequest;
import com.portfolio.aips.project.url_service.protect_url.entity.ProtectURLEntity;
import com.portfolio.aips.project.invite.entity.InviteUserListEntity;
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
    private final InviteRepository inviteRepository;
    private final URLGeneratorService  urlGeneratorService;
    private final URLStatusRepository urlStatusRepository;
    private final InviteService inviteService;


    @Override
    @Transactional
    public String createProtectUrlArchive(InvitedUserCreateRequest request, CustomUserDetails customUserDetails) {
        ProtectURLEntity protectURLEntity = createProtectURLEntity(request, customUserDetails);
        updateUrlStatus(request, protectURLEntity);
        protectURLRepository.save(protectURLEntity);


        InviteEntity invite = inviteRepository
                .findByOwnerUserPkAndTargetType(customUserDetails.getPk(), InviteType.Protect)
                .orElseGet(() -> {
                    InviteEntity newInvite = new InviteEntity();
                    newInvite.setOwnerUserPk(customUserDetails.getPk()); // FK 값 세팅
                    newInvite.setTargetType(InviteType.Protect);
                    newInvite.setMaxInviteCount(20); // 필요 시 기본값 세팅
                    return inviteRepository.save(newInvite); // DB에 저장
                });



        inviteService.saveAll(invite.getPk(), request.getInvitedUserPkList());

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
