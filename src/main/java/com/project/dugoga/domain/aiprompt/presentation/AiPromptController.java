package com.project.dugoga.domain.aiprompt.presentation;

import com.project.dugoga.domain.aiprompt.application.dto.AiPromptCreateRequestDto;
import com.project.dugoga.domain.aiprompt.application.dto.AiPromptCreateResponseDto;
import com.project.dugoga.domain.aiprompt.application.service.AiPromptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
// TODO : 접근권한 및 로그인 체크 추가 필요
public class AiPromptController {

    private final AiPromptService aiPromptService;

    @PostMapping
    public ResponseEntity<AiPromptCreateResponseDto> createAiPrompt(
            @Valid @RequestBody AiPromptCreateRequestDto request)
    {
        AiPromptCreateResponseDto responseDto = aiPromptService.createAiPrompt(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
