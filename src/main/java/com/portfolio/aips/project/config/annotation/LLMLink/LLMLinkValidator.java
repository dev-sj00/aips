package com.portfolio.aips.project.config.annotation.LLMLink;

import com.portfolio.aips.project.utils.enums.LLMUrlPrefix;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class LLMLinkValidator implements ConstraintValidator<CheckLLMUrl, String> {

    private static final String[] allowedUrls;

    static {
        allowedUrls = Arrays.stream(LLMUrlPrefix.values())
                .map(LLMUrlPrefix::getUrl)
                .toArray(String[]::new);
    }

    private String message;


    @Override
    public void initialize(CheckLLMUrl constraintAnnotation) {
        this.message = constraintAnnotation.message(); // 애노테이션에서 메시지 가져오기
    }


    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        for(String prefix : allowedUrls) {

            if(value.startsWith(prefix)) {
                return true;
            }
        }
        String allowedKeys = LLMUrlPrefix.getAllPrefix();

        context.disableDefaultConstraintViolation(); // 기본 메시지 비활성화
        context.buildConstraintViolationWithTemplate(
                message + allowedKeys
        ).addConstraintViolation();

        return false;


    }
}
