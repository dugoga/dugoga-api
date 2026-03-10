package com.project.dugoga.domain.user.presentation.controller;

import com.project.dugoga.domain.user.application.dto.*;
import com.project.dugoga.domain.user.application.service.UserService;
import com.project.dugoga.domain.user.domain.model.enums.UserRoleEnum;
import com.project.dugoga.global.dto.PageResponseDto;
import com.project.dugoga.global.security.jwt.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "회원", description = "회원 인증 및 회원 정보 관련 API")
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "회원가입",
            description = "회원 정보를 입력해 회원 가입합니다. 모든 사용자가 접근 가능합니다."
    )
    @PostMapping("/auth/signup")
    public ResponseEntity<SignupResponseDto> signup(
            @RequestBody @Valid SignupRequestDto requestDto
    ) {
        SignupResponseDto responseDto = userService.signup(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(
            summary = "로그인",
            description = "이메일과 비밀번호로 로그인합니다. 모든 사용자가 접근 가능합니다."
                    + "로그인 성공 시 access token과 refresh token을 발급합니다. "
    )
    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody @Valid LoginRequestDto requestDto
    ) {
        LoginResponseDto responseDto = userService.login(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(
            summary = "로그아웃",
            description = "로그아웃합니다. 로그인한 모든 사용자가 접근 가능합니다."
                    + "Authorization 헤더의 access token을 이용해 로그아웃합니다. "
    )
    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String accessToken
    ) {
        userService.logout(accessToken);
        return ResponseEntity.ok(null);
    }

    @Operation(
            summary = "리프레시 토큰 재발급",
            description = "리프레시 토큰을 재발급합니다. 로그인한 모든 사용자가 접근 가능합니다."
                    + "Authorization 헤더의 refresh token을 이용해 "
                    + "새로운 access token과 refresh token을 재발급합니다."
    )
    @PostMapping("/auth/refresh")
    public ResponseEntity<LoginResponseDto> refresh(
            @RequestHeader("Authorization") String refreshToken
    ) {
        LoginResponseDto responseDto = userService.refresh(refreshToken);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(
            summary = "회원탈퇴",
            description = "회원탈퇴를 합니다. 로그인한 모든 사용자가 접근 가능합니다."
                    + "실제 데이터는 삭제되지 않고 논리 삭제됩니다."
    )
    @DeleteMapping("/auth/withdraw")
    public ResponseEntity<Void> withdraw(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid WithdrawRequestDto requestDto
    ) {
        userService.withdraw(userDetails.getId(), requestDto);
        return ResponseEntity.ok(null);
    }

    @Operation(
            summary = "내 회원 정보 조회",
            description = "현재 로그인한 회원의 정보를 조회합니다. 로그인한 모든 사용자가 접근 가능합니다."
    )
    @GetMapping("/users/my-page")
    public ResponseEntity<UserResponseDto> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UserResponseDto responseDto = userService.getMyInfo(userDetails.getId());
        return ResponseEntity.ok(responseDto);
    }

    @Operation(
            summary = "전체 회원 정보 조회",
            description = "가입한 전체 회원의 정보를 조회합니다. 역할이 'MASTER' 또는 'MANAGER' 인 사용자만 접근 가능합니다."
    )
    @PreAuthorize("hasAnyRole('MASTER', 'MANAGER')")
    @GetMapping("/users")
    public ResponseEntity<PageResponseDto<UserResponseDto>> getUserList(
            @RequestParam(required = false) UserRoleEnum userRole,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<UserResponseDto> page = userService.getAllUsers(userRole, pageable);
        return ResponseEntity.ok(PageResponseDto.from(page));
    }

    @Operation(
            summary = "내 회원 정보 수정",
            description = "현재 로그인한 회원의 정보를 수정합니다. 로그인한 모든 사용자가 접근 가능합니다."
    )
    @PatchMapping("/users/my-page")
    public ResponseEntity<UserResponseDto> updateMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserRequestDto requestDto
    ) {
        UserResponseDto responseDto = userService.updateMyInfo(userDetails.getId(), requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(
            summary = "회원 권한 변경",
            description = "회원의 권한(유형)을 변경합니다. 역할이 'MASTER' 인 사용자만 접근 가능합니다."
    )
    @PreAuthorize("hasRole('MASTER')")
    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<UpdateUserRoleResponseDto> updateUserRole(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRoleRequestDto requestDto
    ) {
        UpdateUserRoleResponseDto responseDto = userService.updateUserRole(userDetails.getId(), userId, requestDto);
        return ResponseEntity.ok(responseDto);
    }
}
