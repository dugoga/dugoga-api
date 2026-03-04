package com.project.dugoga.domain.aiprompt.presentation;

import com.project.dugoga.domain.aiprompt.application.dto.*;
import com.project.dugoga.domain.aiprompt.application.service.AiPromptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews/ai")
// TODO : 접근권한 및 로그인 체크 추가 필요
public class AiPromptController {

    private final AiPromptService aiPromptService;

    @PostMapping
    // TODO : 로그인 이후 authentication에서 user-id 가져오도록 변경 필요
    public ResponseEntity<AiPromptCreateResponseDto> createAiPrompt(
            @Valid @RequestBody AiPromptCreateRequestDto request)
    {
        AiPromptCreateResponseDto responseDto = aiPromptService.createAiPrompt(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PatchMapping("/{promptId}")
    // TODO : 로그인 기능 구현 이후 기존 등록자와 재등록 요청자 비교 추가 필요
    public ResponseEntity<AiPromptRecreateResponseDto> recreateAiPrompt(
            @PathVariable UUID promptId, @Valid @RequestBody AiPromptRecreateRequestDto request)
    {
        AiPromptRecreateResponseDto responseDto = aiPromptService.recreateAiPrompt(promptId, request);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/{promptId}")
    public ResponseEntity<AiPromptGetResponseDto> getAiPrompt(
            @PathVariable UUID promptId)
    {
        AiPromptGetResponseDto responseDto = aiPromptService.getAiPrompt(promptId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/{promptId}")
    // TODO : 로그인 기능 구현 이후 삭제 요청자와 작성자 검증 로직 필요
    public ResponseEntity<Void> deleteAiPrompt(@PathVariable UUID promptId)
    {
        // TODO : 로그인 기능 구현 이후 userId 가져와서 전달
        Long userId = 1L;
        aiPromptService.deleteAiPrompt(promptId, userId);
        return ResponseEntity.noContent().build();
    }


}
