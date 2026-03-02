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
public class StoreUpdateResponseDto {
    private final UUID id;
    private final LocalDateTime updatedAt;

    public static StoreUpdateResponseDto from(Store store) {
        return StoreUpdateResponseDto.builder()
                .id(store.getId())
                .updatedAt(store.getUpdatedAt())
                .build();
    }
}
