package com.project.dugoga.domain.image.application.dto;

import com.project.dugoga.domain.image.domain.model.enums.FileType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ImageUpdateRequestDto {
    @NotBlank
    private String fileName;

    @NotNull
    private FileType fileType;

    @NotNull
    private String deleteUrl;
}
