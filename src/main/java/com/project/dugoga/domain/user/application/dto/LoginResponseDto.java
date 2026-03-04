package com.project.dugoga.domain.user.application.dto;

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
}
