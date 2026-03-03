package com.project.dugoga.domain.aiprompt.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class AiPromptCreateRequestDto {

    @NotNull
    @JsonProperty("user-id")
    private Long userId;

    @NotNull
    @JsonProperty("store-id")
    private UUID storeId;

    @NotNull
    @JsonProperty("product-id")
    private UUID productId;

    @NotNull
    @JsonProperty("prompt-text")
    private String promptText;

}
