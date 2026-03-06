package com.project.dugoga.domain.product.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class ProductStatusUpdateResponseDto {
    private final List<UUID> successIds;
    private final List<UUID> failIds;
    private final String message;
    private final LocalDateTime updatedAt;

    public static ProductStatusUpdateResponseDto of(List<UUID> successIds, List<UUID> failIds, LocalDateTime updatedAt) {
        return ProductStatusUpdateResponseDto.builder()
                .successIds(successIds)
                .failIds(failIds)
                .message(String.format("총 %d건 중 %d건 성공, %d건 실패",
                        successIds.size() + failIds.size(), successIds.size(), failIds.size()))
                .updatedAt(updatedAt)
                .build();
    }
}
