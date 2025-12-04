package com.portfolio.aips.project.url_service.protect_url.service.protect_url.create_protect_url;


import com.portfolio.aips.project.url_service.protect_url.dto.request.BaseCreateProtectURLRequest;
import com.portfolio.aips.project.users.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProtectURLServiceCreateMethod {

    private final Map<String, ProtectURLCreateService<?>> protectURLCreateServices;



    @Transactional
    public<T extends BaseCreateProtectURLRequest> String createProtectUrl(T requestDTO, CustomUserDetails customUserDetails)
    {



       String requestClassName = requestDTO.getClass().getSimpleName();
       String serviceName = convertDtoClassNameToServiceName(requestClassName);

       @SuppressWarnings("unchecked")
       ProtectURLCreateService<T> service = (ProtectURLCreateService<T>) protectURLCreateServices.get(serviceName);


       return service.createProtectUrlArchive(requestDTO, customUserDetails);
    }

    private String convertDtoClassNameToServiceName(String dtoClassName)
    {
        if(dtoClassName.endsWith("Request")) {
            dtoClassName = dtoClassName.substring(0, dtoClassName.length() - "Request".length());
            log.info("dtoClassName = {}", dtoClassName);
        }


        return dtoClassName + "Service";
    }
}
