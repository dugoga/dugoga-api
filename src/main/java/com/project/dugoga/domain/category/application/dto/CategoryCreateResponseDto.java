package com.project.dugoga.domain.category.application.dto;

import com.project.dugoga.domain.category.domain.model.entity.Category;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryCreateResponseDto {

    private UUID id;
    private LocalDateTime createdAt;

    public static CategoryCreateResponseDto from(Category category) {
        return CategoryCreateResponseDto.builder()
                .id(category.getId())
                .createdAt(category.getCreatedAt())
                .build();
    }
}
