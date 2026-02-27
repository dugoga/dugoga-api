package com.project.dugoga.global.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse<T> {

    private final boolean success = false;
    private final T message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime timestamp;

    private ErrorResponse(T message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ErrorResponse<T> of(T message) {
        return new ErrorResponse<>(message);
    }
}
