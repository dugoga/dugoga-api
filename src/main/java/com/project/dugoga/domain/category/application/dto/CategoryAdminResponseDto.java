package com.project.dugoga.domain.category.application.dto;

import com.project.dugoga.domain.category.domain.model.entity.Category;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class CategoryAdminResponseDto {

    private UUID id;
    private String name;
    private LocalDateTime deletedAt;

    public static CategoryAdminResponseDto from(Category category) {
        return CategoryAdminResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .deletedAt(category.getDeletedAt())
                .build();
    }
}
