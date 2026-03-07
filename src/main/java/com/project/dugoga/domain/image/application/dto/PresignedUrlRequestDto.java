package com.project.dugoga.domain.image.application.dto;

import com.project.dugoga.domain.image.domain.model.enums.FileType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PresignedUrlRequestDto {
    @NotBlank
    private String fileName;

    @NotNull
    private FileType fileType;
}
