package com.project.dugoga.domain.aiprompt.application.dto;

import com.project.dugoga.domain.aiprompt.domain.model.entity.AiPrompt;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class AiPromptRestoreResponseDto {

    private UUID id;
    private LocalDateTime updatedAt;

    public static AiPromptRestoreResponseDto from(AiPrompt aiPrompt) {
        return new AiPromptRestoreResponseDto(aiPrompt.getId(), aiPrompt.getUpdatedAt());
    }
}
