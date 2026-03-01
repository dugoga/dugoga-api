package com.project.dugoga.global.exception;

import com.project.dugoga.global.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse<String>> handleBusinessException(BusinessException ex) {
        HttpStatus httpStatus = ex.getHttpStatus();
        return ResponseEntity
                .status(httpStatus)
                .body(ErrorResponse.of(ex.getMessage()));
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
