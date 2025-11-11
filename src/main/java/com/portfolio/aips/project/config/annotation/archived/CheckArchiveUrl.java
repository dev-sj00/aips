package com.portfolio.aips.project.config.annotation.archived;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;

@Documented
@Constraint(validatedBy = ArchiveLinkValidator.class)
public @interface CheckArchiveUrl {
    
    String message() default "archiveLink가 올바르지 않습니다."; //에러시 기본 메시지
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {}; // 메타데이터

}
