package com.project.dugoga.domain.aiprompt.presentation;

import com.project.dugoga.domain.aiprompt.application.dto.*;
import com.project.dugoga.domain.aiprompt.application.service.AiPromptService;
import com.project.dugoga.global.security.jwt.CustomUserDetails;
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
public class AiPromptController {

    private final AiPromptService aiPromptService;

    @PostMapping
    public ResponseEntity<AiPromptCreateResponseDto> createAiPrompt(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AiPromptCreateRequestDto request)
    {
        AiPromptCreateResponseDto responseDto = aiPromptService.createAiPrompt(request, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PatchMapping("/{promptId}")
    public ResponseEntity<AiPromptRecreateResponseDto> recreateAiPrompt(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID promptId, @Valid @RequestBody AiPromptRecreateRequestDto request)
    {
        AiPromptRecreateResponseDto responseDto = aiPromptService.recreateAiPrompt(request, promptId, userDetails.getId());
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    // GET은 누구가 접근 가능
    @GetMapping("/{promptId}")
    public ResponseEntity<AiPromptGetResponseDto> getAiPrompt(
            @PathVariable UUID promptId)
    {
        AiPromptGetResponseDto responseDto = aiPromptService.getAiPrompt(promptId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/{promptId}")
    public ResponseEntity<Void> deleteAiPrompt(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable UUID promptId)
    {
        aiPromptService.deleteAiPrompt(promptId, userDetails);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{promptId}/restore")
    public ResponseEntity<AiPromptRestoreResponseDto> restoreAiPrompt(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable UUID promptId)
    {
        AiPromptRestoreResponseDto responseDto = aiPromptService.restoreAiPrompt(promptId, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

}
