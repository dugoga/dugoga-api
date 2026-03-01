package com.project.dugoga.domain.category.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryRestoreResponseDto {

    private UUID id;
    private LocalDateTime updatedAt;

    public CategoryRestoreResponseDto(UUID id, LocalDateTime updatedAt) {
        this.id = id;
        this.updatedAt = updatedAt;
    }
}
