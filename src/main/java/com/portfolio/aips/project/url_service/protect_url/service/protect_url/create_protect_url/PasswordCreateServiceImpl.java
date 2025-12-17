package com.portfolio.aips.project.url_service.protect_url.service.protect_url.create_protect_url;

import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;
import com.portfolio.aips.project.url_service.common.entity.URLStatusEntity;
import com.portfolio.aips.project.url_service.common.repo.URLStatusRepository;
import com.portfolio.aips.project.url_service.common.service.url_generator.URLGeneratorService;
import com.portfolio.aips.project.url_service.common.service.url_generator.enums.URLGeneratorType;
import com.portfolio.aips.project.url_service.protect_url.dto.request.InvitedUserCreateRequest;
import com.portfolio.aips.project.url_service.protect_url.dto.request.PasswordCreateRequest;
import com.portfolio.aips.project.url_service.protect_url.entity.ProtectURLEntity;
import com.portfolio.aips.project.url_service.protect_url.repo.ProtectURLRepository;
import com.portfolio.aips.project.users.dto.CustomUserDetails;
import com.portfolio.aips.project.utils.enums.LLMType;
import com.portfolio.aips.project.utils.enums.LLMUrlPrefix;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("PasswordCreateService")
@RequiredArgsConstructor
public class PasswordCreateServiceImpl extends ProtectURLCreateService<PasswordCreateRequest> {

    private final URLGeneratorService  urlGeneratorService;
    private final ProtectURLRepository protectURLRepository;
    private final URLStatusRepository urlStatusRepository;


    @Override

    public String createProtectUrlArchive(PasswordCreateRequest createProtectURLRequest, CustomUserDetails customUserDetails) {

        ProtectURLEntity protectURLEntity = createProtectURLEntity(createProtectURLRequest, customUserDetails);

        updateUrlStatus(createProtectURLRequest, protectURLEntity);


        protectURLRepository.save(protectURLEntity);


        return protectURLEntity.getSiteSlug();

    }


    protected void updateUrlStatus(PasswordCreateRequest request, ProtectURLEntity protectURLEntity) {
        URLStatusEntity urlStatusEntity = urlStatusRepository
                .findByIsCreatedAndUrlLink(false, request.getUrlLink())
                .orElseThrow(() -> new CustomException(ErrorCode.URL_NOT_FOUND));

        urlStatusEntity.setCreated(true);

        protectURLEntity.setUrlStatusEntity(urlStatusEntity);
    }

    protected ProtectURLEntity createProtectURLEntity(PasswordCreateRequest createProtectURLRequest, CustomUserDetails customUserDetails) {
       ProtectURLEntity protectURLEntity = new ProtectURLEntity();
       protectURLEntity.setWriterPk(customUserDetails.getPk());

       if(!protectURLEntity.isConfirmURLPassword(createProtectURLRequest.getPassword(), createProtectURLRequest.getConfirmPassword())){
            throw new CustomException(ErrorCode.INVALID_URL_PASSWORD);
       }


       protectURLEntity.setUrlPassword(createProtectURLRequest.getPassword());
       LLMType llmType = LLMType.valueOf(LLMUrlPrefix.findKeyByUrl(createProtectURLRequest.getUrlLink()));
       protectURLEntity.setLlmType(llmType);

       String siteSlug = urlGeneratorService.createUrlProc(customUserDetails.getPk(), URLGeneratorType.Protector);
       protectURLEntity.setSiteSlug(siteSlug);
       protectURLEntity.setDescription(createProtectURLRequest.getDescription());
       protectURLEntity.setTitle(createProtectURLRequest.getTitle());



       return protectURLEntity;



    }
}
