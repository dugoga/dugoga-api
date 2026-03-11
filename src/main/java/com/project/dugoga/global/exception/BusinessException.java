package com.project.dugoga.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String message;

    // 예외 던질시 기본 메세지 출력
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.httpStatus = errorCode.getStatus();
        this.message = errorCode.getDefaultMessage();
    }

    // 예외 던질시 메세지 출력
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.httpStatus = errorCode.getStatus();
        this.message = message;
    }
}
