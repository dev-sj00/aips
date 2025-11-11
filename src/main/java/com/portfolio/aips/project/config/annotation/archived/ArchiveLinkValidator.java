package com.portfolio.aips.project.config.annotation.archived;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class ArchiveLinkValidator implements ConstraintValidator<CheckArchiveUrl, String> {

    private static final String[] allowedUrls;

    static {
        allowedUrls = Arrays.stream(ArchiveUrlPrefix.values())
                .map(ArchiveUrlPrefix::getUrl)
                .toArray(String[]::new);
    }

    private String message;


    @Override
    public void initialize(CheckArchiveUrl constraintAnnotation) {
        this.message = constraintAnnotation.message(); // 애노테이션에서 메시지 가져오기
    }


    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        for(String prefix : allowedUrls) {

            if(value.startsWith(prefix)) {
                return true;
            }
        }
        String allowedKeys = ArchiveUrlPrefix.getAllPrefix();

        context.disableDefaultConstraintViolation(); // 기본 메시지 비활성화
        context.buildConstraintViolationWithTemplate(
                message + allowedKeys
        ).addConstraintViolation();

        return false;


    }
}
