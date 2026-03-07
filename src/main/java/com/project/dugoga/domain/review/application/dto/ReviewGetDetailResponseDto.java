package com.project.dugoga.domain.review.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.dugoga.domain.review.domain.model.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class ReviewGetDetailResponseDto {

    @JsonProperty("review-id")
    private UUID id;
    @JsonProperty("store-id")
    private UUID storeId;
    @JsonProperty("user-id")
    private Long userId;
    @JsonProperty("order-id")
    private UUID orderId;

    private Integer rating;
    private String content;

    @JsonProperty("image-url")
    private String imageUrl;
    @JsonProperty("created-at")
    private LocalDateTime createdAt;
    @JsonProperty("created-by")
    private Long createdBy;
    @JsonProperty("updated-at")
    private LocalDateTime updatedAt;
    @JsonProperty("updated-by")
    private Long updatedBy;

    public static ReviewGetDetailResponseDto from(Review review) {
        return ReviewGetDetailResponseDto.builder()
                .id(review.getId())
                .storeId(review.getStoreId().getId())
                .userId(review.getUserId().getId())
                .orderId(review.getOrderId().getId())
                .rating(review.getRating())
                .content(review.getContent())
                .imageUrl(review.getImageUrl())
                .createdAt(review.getCreatedAt())
                .createdBy(review.getCreatedBy())
                .updatedAt(review.getUpdatedAt())
                .updatedBy(review.getUpdatedBy())
                .build();
    }
}