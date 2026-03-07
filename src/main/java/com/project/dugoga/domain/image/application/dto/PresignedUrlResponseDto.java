package com.project.dugoga.domain.image.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PresignedUrlResponseDto {
    private final String uploadUrl;
    private final String fileUrl;

    @Builder
    public PresignedUrlResponseDto(String uploadUrl, String fileUrl) {
        this.uploadUrl = uploadUrl;
        this.fileUrl = fileUrl;
    }

}
