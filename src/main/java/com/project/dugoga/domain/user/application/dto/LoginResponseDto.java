package com.project.dugoga.domain.user.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponseDto {
    private String id;
    private String name;
    private String accessToken;
    private String refreshToken;
    private final LocalDateTime createdAt;

    public static LoginResponseDto of(Long userId, String name, String accessToken, String refreshToken) {
        return LoginResponseDto.builder()
                .id(String.valueOf(userId))
                .name(name)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
