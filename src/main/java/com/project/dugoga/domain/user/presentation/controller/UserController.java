package com.project.dugoga.domain.user.presentation.controller;

import com.project.dugoga.domain.user.application.dto.LoginRequestDto;
import com.project.dugoga.domain.user.application.dto.LoginResponseDto;
import com.project.dugoga.domain.user.application.dto.RegisterRequestDto;
import com.project.dugoga.domain.user.application.dto.WithdrawRequestDto;
import com.project.dugoga.domain.user.application.service.UserService;
import com.project.dugoga.global.security.jwt.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    //회원가입
    @PostMapping("/auth/signup")
    public ResponseEntity<Void> register(
            @RequestBody @Valid RegisterRequestDto requestDto
    ) {
        userService.register(requestDto);
        return ResponseEntity.ok(null);
    }

    //로그인
    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody @Valid LoginRequestDto requestDto
    ) {
        LoginResponseDto response = userService.login(requestDto);
        return ResponseEntity.ok(response);
    }

    //로그아웃
    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String accessToken
    ) {
        userService.logout(accessToken);
        return ResponseEntity.ok(null);
    }

    //토큰재발급
    @PostMapping("/auth/refresh")
    public ResponseEntity<LoginResponseDto> refresh(
            @RequestHeader("Authorization") String refreshToken
    ) {
        LoginResponseDto response = userService.refresh(refreshToken);
        return ResponseEntity.ok(response);
    }

    //회원탈퇴
    @DeleteMapping("/auth/withdraw")
    public ResponseEntity<Void> withdraw(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid WithdrawRequestDto requestDto
    ) {
        userService.withdraw(userDetails.getId(), requestDto);
        return ResponseEntity.ok(null);
    }
}
