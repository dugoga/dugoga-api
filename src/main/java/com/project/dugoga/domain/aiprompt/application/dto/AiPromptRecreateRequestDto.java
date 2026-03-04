package com.project.dugoga.domain.aiprompt.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AiPromptRecreateRequestDto {

    @NotNull
    @JsonProperty("prompt-text")
    private String promptText;

}
