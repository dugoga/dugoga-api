package com.project.dugoga.domain.user.presentation.controller;

import com.project.dugoga.domain.user.application.dto.*;
import com.project.dugoga.domain.user.application.service.UserService;
import com.project.dugoga.global.security.jwt.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<SignupResponseDto> signup(
            @RequestBody @Valid SignupRequestDto requestDto
    ) {
        SignupResponseDto responseDto = userService.signup(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    //로그인
    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody @Valid LoginRequestDto requestDto
    ) {
        LoginResponseDto responseDto = userService.login(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    //로그아웃
    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String accessToken
    ) {
        userService.logout(accessToken);
        return ResponseEntity.ok(null);
    }

    //토큰 재발급
    @PostMapping("/auth/refresh")
    public ResponseEntity<LoginResponseDto> refresh(
            @RequestHeader("Authorization") String refreshToken
    ) {
        LoginResponseDto responseDto = userService.refresh(refreshToken);
        return ResponseEntity.ok(responseDto);
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

    // 회원 정보 조회
    @GetMapping("/users/my-page")
    public ResponseEntity<UserResponseDto> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UserResponseDto responseDto = userService.getMyInfo(userDetails.getId());
        return ResponseEntity.ok(responseDto);
    }

    // 회원 정보 수정
    @PatchMapping("/users/my-page")
    public ResponseEntity<UserResponseDto> updateMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserRequestDto requestDto
    ) {
        UserResponseDto responseDto = userService.updateMyInfo(userDetails.getId(), requestDto);
        return ResponseEntity.ok(responseDto);
    }
}
