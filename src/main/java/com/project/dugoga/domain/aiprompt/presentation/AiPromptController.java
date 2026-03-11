package com.project.dugoga.domain.aiprompt.presentation;

import com.project.dugoga.domain.aiprompt.application.dto.*;
import com.project.dugoga.domain.aiprompt.application.service.AiPromptService;
import com.project.dugoga.global.security.jwt.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai/descriptions")
@Tag(name = "AI 프롬프트", description = "AI 상품 설명 생성 API")
public class AiPromptController {

    private final AiPromptService aiPromptService;

    @Operation(
            summary = "AI 프롬프트 상품 설명 생성",
            description = "AI를 활용하여 상품(음식) 설명을 생성합니다. <br>" +
                    "로그인한 사용자만 접근 가능합니다."
    )
    @PostMapping
    public ResponseEntity<AiPromptCreateResponseDto> createAiPrompt(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AiPromptCreateRequestDto request)
    {
        AiPromptCreateResponseDto responseDto = aiPromptService.createAiPrompt(request, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(
            summary = "AI 프롬프트 재생성",
            description = "기존 상품 설명이 마음에 들지 않는 경우," +
                    "프롬프트의 텍스트를 수정하여 응답을 다시 생성하고 업데이트합니다. <br>" +
                    "해당 프롬프트를 작성한 본인만 접근 가능합니다."
    )
    @PatchMapping("/{promptId}")
    public ResponseEntity<AiPromptRecreateResponseDto> recreateAiPrompt(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID promptId, @Valid @RequestBody AiPromptRecreateRequestDto request)
    {
        AiPromptRecreateResponseDto responseDto = aiPromptService.recreateAiPrompt(request, promptId, userDetails.getId());
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    // GET은 누구가 접근 가능
    @Operation(
            summary = "AI 프롬프트 상세 조회",
            description = "생성된 개별 요청 프롬프트와 응답 결과를 조회합니다. <br>" +
                    "누구나 접근 가능합니다."
    )
    @GetMapping("/{promptId}")
    public ResponseEntity<AiPromptGetResponseDto> getAiPrompt(
            @PathVariable UUID promptId)
    {
        AiPromptGetResponseDto responseDto = aiPromptService.getAiPrompt(promptId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(
            summary = "AI 프롬프트 삭제",
            description = "생성된 AI 상품 설명을 Soft Delete로 삭제합니다. <br>" +
                    "프롬프트를 작성한 본인 또는 'MASTER', 'MANAGER' 권한을 가진 사용자만 접근 가능합니다."
    )
    @DeleteMapping("/{promptId}")
    public ResponseEntity<Void> deleteAiPrompt(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable UUID promptId)
    {
        aiPromptService.deleteAiPrompt(promptId, userDetails);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "AI 프롬프트 복구",
            description = "삭제된 AI 상품 설명을 복구합니다. <br>" +
                    "'MASTER', 'MANAGER' 권한을 가진 사용자만 접근 가능합니다."
    )
    @PatchMapping("/{promptId}/restore")
    public ResponseEntity<AiPromptRestoreResponseDto> restoreAiPrompt(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable UUID promptId)
    {
        AiPromptRestoreResponseDto responseDto = aiPromptService.restoreAiPrompt(promptId, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

}
