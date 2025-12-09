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
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "ACCOUNT-005", "유저 정보가 없습니다."),
    INVALID_SOCIAL_REFRESH_TOKEN(HttpStatus.CONFLICT, "ACCOUNT-006", "SNS 로그인 토큰 문제로 로그아웃되었습니다."),
    URL_UNREACHABLE(HttpStatus.BAD_REQUEST, "URL-VERIFY-001", "LLM 사이트에 문제가 생겼습니다."),
    URL_NOT_FOUND(HttpStatus.NOT_FOUND, "URL-VERIFY-002", "LLM 사이트 대화 정보를 찾지 못했습니다!"),
    URL_ALREADY_EXISTS(HttpStatus.CONFLICT, "URL-VERIFY-003", "LLM 사이트가 이미 등록 되었습니다!"),
    INVALID_URL_PASSWORD(HttpStatus.UNAUTHORIZED, "URL-VERIFY-004", "비밀번호가 맞지 않습니다!"),
    URL_FORBIDDEN_USER(HttpStatus.FORBIDDEN, "URL-VERIFY-005", "허가되지 않은 사용자입니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다!"),
    MAX_FAVORITE_FRIEND_REACHED(HttpStatus.BAD_REQUEST, "INVITE-001", "찜한 친구 최대 인원에 도달했습니다!");

    private final HttpStatus httpStatus;
    private final String code;
    private final String detail;
}
