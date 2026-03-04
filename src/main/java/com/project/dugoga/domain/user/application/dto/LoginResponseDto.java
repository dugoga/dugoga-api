package com.project.dugoga.domain.user.application.dto;

import com.project.dugoga.domain.user.domain.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponseDto {
    private String id;
    private String name;
    private String accessToken;
    private String refreshToken;

    public static LoginResponseDto of(Long userId, String name, String accessToken, String refreshToken) {
        return LoginResponseDto.builder()
                .id(String.valueOf(userId))
                .name(name)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
