package com.project.dugoga.domain.product.application.dto;

import com.project.dugoga.domain.product.domain.model.entity.Product;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class ProductDetailsResponseDto {
    private UUID id;
    private UUID storeId;
    private String name;
    private String comment;
    private Integer price;
    private String imageUrl;
    private Boolean isSoldOut;
    private Boolean isHidden;
    private LocalDateTime createdAt;
    private Long createdBy;
    private LocalDateTime updatedAt;
    private Long updatedBy;

    public static ProductDetailsResponseDto from(Product product, Store store) {
        return ProductDetailsResponseDto.builder()
                .id(product.getId())
                .storeId(store.getId())
                .name(product.getName())
                .comment(product.getComment())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .isSoldOut(product.getIsSoldOut())
                .isHidden(product.getIsHidden())
                .createdAt(product.getCreatedAt())
                .createdBy(product.getCreatedBy())
                .updatedAt(product.getUpdatedAt())
                .updatedBy(product.getUpdatedBy())
                .build();
    }
}
