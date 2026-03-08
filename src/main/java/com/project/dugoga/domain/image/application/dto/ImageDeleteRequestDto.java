package com.project.dugoga.domain.image.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ImageDeleteRequestDto {
    @NotBlank
    private String fileUrl;
}
