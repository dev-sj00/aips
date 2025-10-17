package com.portfolio.aips.project.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    DEVICE_NOT_FOUND(HttpStatus.NOT_FOUND, "ACCOUNT-001", "장시간 미접속으로 로그아웃 합니다."),
    UNAUTHORIZED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "ACCOUNT-002", "액세스 토큰이 유효하지 않습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "ACCOUNT-003", "다른 환경에서 로그인 되었습니다!"),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "ACCOUNT-004", "장시간 미접속으로 로그아웃 합니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String detail;
}
