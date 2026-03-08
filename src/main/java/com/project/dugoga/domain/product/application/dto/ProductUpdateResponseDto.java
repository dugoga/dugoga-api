package com.project.dugoga.domain.product.application.dto;

import com.project.dugoga.domain.product.domain.model.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class ProductUpdateResponseDto {
    private UUID id;
    private LocalDateTime updatedAt;

    public static ProductUpdateResponseDto from(Product product) {
        return ProductUpdateResponseDto.builder()
                .id(product.getId())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

}
