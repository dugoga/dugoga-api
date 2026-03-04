package com.project.dugoga.domain.store.application.dto;

import com.project.dugoga.domain.store.domain.model.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class StoreCreateResponseDto {
    private final UUID id;
    private final LocalDateTime createdAt;

    public static StoreCreateResponseDto from(Store store) {
        return StoreCreateResponseDto.builder()
                .id(store.getId())
                .createdAt(store.getCreatedAt())
                .build();
    }
}
