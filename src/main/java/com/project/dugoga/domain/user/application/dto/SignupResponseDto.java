package com.project.dugoga.domain.user.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SignupResponseDto {
    private Long userId;
    private final LocalDateTime createdAt;
}
