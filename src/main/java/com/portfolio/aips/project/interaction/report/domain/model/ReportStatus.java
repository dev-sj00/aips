package com.portfolio.aips.project.interaction.report.domain.model;


//pending-> In_progress 확인 절차(이전 신고내역, 게시글 확인)
// -> completed (active_sanction_entity 제재 중임)
public enum ReportStatus {
    PENDING,        // 미처리 (아직 판단 전)
    IN_PROGRESS,    // 제재 처리중 (조사/검토/적용중)
    CONFIRMED, //제재 확정
    COMPLETED,      // 제재 완료 (정지 적용)
    DUPLICATED,     // 동일 사안 (기존 제재에 병합)
    CANCELLED, //제재 취소
}
