package com.project.dugoga.global.security.jwt;

import com.project.dugoga.domain.user.domain.model.enums.UserRoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final JwtUtil jwtUtil;

    public String extractUserId(String token) {
        return jwtUtil.extractAllClaims(token).getSubject();
    }

    public String substringToken(String tokenValue) {
        return jwtUtil.substringToken(tokenValue);
    }

    public String createRefreshToken(Long userId) {
        return jwtUtil.createRefreshToken(userId);
    }

    public String createAccessToken(Long userId, UserRoleEnum userRole) {
        return jwtUtil.createAccessToken(userId, userRole);
    }

    public boolean isValidRefreshToken(String refreshToken) {
        return jwtUtil.isValidRefreshToken(refreshToken);
    }
}
