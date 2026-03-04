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
public class ProductCreateResponseDto {
    private final UUID id;
    private final LocalDateTime createdAt;

    public static ProductCreateResponseDto from(Product product) {
        return ProductCreateResponseDto.builder()
                .id(product.getId())
                .createdAt(product.getCreatedAt())
                .build();

    }
}
