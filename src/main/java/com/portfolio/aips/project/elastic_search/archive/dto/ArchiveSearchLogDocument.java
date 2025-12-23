package com.portfolio.aips.project.elastic_search.archive.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ArchiveSearchLogDocument {
    private String queryRaw;           // 사용자가 입력한 원문
    private String queryStat;          // 통계용 분석 필드
    private List<String> tokens;       // 형태소 분석 토큰값들
    private boolean hasFiltered;       // 통계 제외 여부
    private String userNickName;             // optional
    private LocalDateTime createdDateTime;   // 생성 시간
}
