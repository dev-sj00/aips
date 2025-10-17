package com.portfolio.aips.project.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter

public class CustomException extends RuntimeException{
    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getDetail()); // RuntimeException에 메시지 전달
        this.errorCode = errorCode;
    }
}
