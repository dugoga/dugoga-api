package com.project.dugoga.domain.user.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class LoginResponseDto {
    private String id;
    private String name;
    private String accessToken;
    private String refreshToken;
}
