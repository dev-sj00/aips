package com.portfolio.aips.project.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    DEVICE_NOT_FOUND(HttpStatus.UNAUTHORIZED, "ACCOUNT-001", "장시간 미접속으로 인해 로그아웃되었습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "ACCOUNT-002", "다른 기기에서 로그인되어 로그아웃되었습니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "ACCOUNT-003", "장시간 미접속으로 인해 로그아웃되었습니다."),
    TOKEN_PAIR_MISMATCH(HttpStatus.CONFLICT, "ACCOUNT-004", "토큰이 변조되어 로그아웃되었습니다."),
    INVALID_SOCIAL_REFRESH_TOKEN(HttpStatus.CONFLICT, "ACCOUNT-005", "SNS 로그인 토큰 문제로 로그아웃되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String detail;
}
