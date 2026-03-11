package com.project.dugoga.domain.aiprompt.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.dugoga.domain.aiprompt.domain.model.entity.AiPrompt;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class AiPromptGetResponseDto {

    @JsonProperty("aiPrompt-id")
    private UUID id;
    @JsonProperty("user-id")
    private Long userId;
    @JsonProperty("store-id")
    private UUID storeId;
    @JsonProperty("product-id")
    private UUID productId;
    @JsonProperty("prompt-text")
    private String promptText;
    @JsonProperty("response-text")
    private String responseText;

    @JsonProperty("created-at")
    private LocalDateTime createdAt;
    @JsonProperty("created-by")
    private Long  createdBy;
    @JsonProperty("updated-at")
    private LocalDateTime updatedAt;
    @JsonProperty("updated-by")
    private Long updatedBy;
    @JsonProperty("deleted-at")
    private LocalDateTime deletedAt;
    @JsonProperty("deleted-by")
    private Long deletedBy;

    public static AiPromptGetResponseDto from(AiPrompt aiPrompt) {
        return AiPromptGetResponseDto.builder()
                .id(aiPrompt.getId())
                .userId(aiPrompt.getUser().getId())
                .storeId(aiPrompt.getStore().getId())
                .productId(aiPrompt.getProduct().getId())
                .promptText(aiPrompt.getPromptText())
                .responseText(aiPrompt.getResponseText())
                .createdAt(aiPrompt.getCreatedAt())
                .createdBy(aiPrompt.getCreatedBy())
                .updatedAt(aiPrompt.getUpdatedAt())
                .updatedBy(aiPrompt.getUpdatedBy())
                .deletedAt(aiPrompt.getDeletedAt())
                .deletedBy(aiPrompt.getDeletedBy())
                .build();
    }
}
