package com.portfolio.aips.project.url_service.common.service.url_status;

import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;
import com.portfolio.aips.project.url_service.common.dto.commnad.CreateURLStatusCommand;
import com.portfolio.aips.project.url_service.common.entity.URLStatusEntity;
import com.portfolio.aips.project.url_service.common.repo.URLStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class URLStatusServiceImpl implements URLStatusService
{
    private final URLStatusRepository urlStatusRepository;

    @Override
    public void createURLStatusProc(CreateURLStatusCommand createURLStatusCommand) {

        boolean isExistURL = urlStatusRepository.existsByIsCreatedAndUrlLink(false, createURLStatusCommand.urlLink());

        if (isExistURL) {
            throw new CustomException(ErrorCode.URL_ALREADY_EXISTS);
        }


        URLStatusEntity urlStatusEntity = URLStatusEntity
                    .builder()
                    .urlLink(createURLStatusCommand.urlLink())
                    .urlType(createURLStatusCommand.urlType())
                    .build();

        urlStatusRepository.save(urlStatusEntity);
    }




}
