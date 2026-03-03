package com.project.dugoga.domain.aiprompt.application.dto;

import com.project.dugoga.domain.aiprompt.domain.model.entity.AiPrompt;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class AiPromptCreateResponseDto {

    private UUID id;
    private LocalDateTime createdAt;

    public static AiPromptCreateResponseDto from(AiPrompt aiPrompt) {
        return AiPromptCreateResponseDto.builder()
                .id(aiPrompt.getId())
                .createdAt(aiPrompt.getCreatedAt())
                .build();
    }
}
