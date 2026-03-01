package com.project.dugoga.domain.category.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryCreateResponseDto {

    private UUID id;
    private LocalDateTime createdAt;

    public CategoryCreateResponseDto(UUID id, LocalDateTime createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }
}
