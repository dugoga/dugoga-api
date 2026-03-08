package com.project.dugoga.global.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.project.dugoga.global.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        FieldError firstError = ex.getBindingResult().getFieldError();
        String message = "유효성 검사에 실패했습니다.";
        if (firstError != null) {
            StringBuilder errorMessage = new StringBuilder();
            ex.getBindingResult().getAllErrors().forEach(e ->
                    errorMessage.append(e.getDefaultMessage()).append(System.lineSeparator()));
            message = errorMessage.toString();
        }

        return ResponseEntity
                .status(httpStatus)
                .body(ErrorResponse.of(message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse<String>> handleReadableException(HttpMessageNotReadableException ex) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String message = "입력값 파싱에 실패했습니다.";
        Throwable cause = ex.getCause();
        if (cause instanceof JsonParseException) {
            message = "JSON 형식이 올바르지 않습니다";
        } else if (cause instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) cause;
            message = String.format("잘못된 타입입니다. [입력값:'%s']", ife.getValue());
        }

        return ResponseEntity
                .status(httpStatus)
                .body(ErrorResponse.of(message));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse<String>> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        String headerName = ex.getHeaderName();
        HttpStatus httpStatus;
        String message;

        if ("Authorization".equalsIgnoreCase(headerName)) {
            httpStatus = HttpStatus.UNAUTHORIZED;
            message = "JWT 토큰이 필요합니다.";
        } else {
            httpStatus = HttpStatus.BAD_REQUEST;
            message = String.format("필수 헤더가 누락되었습니다. [%s]", headerName);
        }

        return ResponseEntity
                .status(httpStatus)
                .body(ErrorResponse.of(message));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse<String>> handleBusinessException(BusinessException ex) {
        HttpStatus httpStatus = ex.getHttpStatus();
        return ResponseEntity
                .status(httpStatus)
                .body(ErrorResponse.of(ex.getMessage()));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse<String>> handleAccessDenied(Exception ex) {
        ErrorCode errorCode = ErrorCode.FORBIDDEN;
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode.getDefaultMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse<String>> handleGlobalException(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        return ResponseEntity
                .status(httpStatus)
                .body(ErrorResponse.of("Internal Server Error"));
    }

}
