package com.portfolio.aips.project.interaction.report.domain.model;

public enum ReportType {
    SPAM,               // 스팸 / 광고
    ABUSE,              // 욕설 / 비하 / 인신공격
    SEXUAL_CONTENT,     // 음란물 / 선정적 콘텐츠
    VIOLENCE_CONTENT,           // 폭력 / 잔인한 콘텐츠
    ILLEGAL_CONTENT,    // 불법 정보 / 범죄 조장
    FRAUD,              // 사기 / 허위 정보
    COPYRIGHT,          // 저작권 침해
    PERSONAL_INFO,      // 개인정보 노출
    ETC                 // 기타
}
