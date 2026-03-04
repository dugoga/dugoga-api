package com.project.dugoga.domain.store.application.dto;

import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.store.domain.model.enums.StoreStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class StoreDetailsResponseDto {

    private UUID id;
    private String name;
    private Long userId;
    private String userName;
    private String userNickname;
    private String categoryCode;
    private String categoryName;
    private StoreStatus status;
    private Boolean isHidden;
    private Double averageRating;
    private Long reviewCount;
    private String addressName;
    private String region1depthName;
    private String region2depthName;
    private String region3depthName;
    private String detailAddress;
    private Double longitude;
    private Double latitude;
    private String comment;
    private LocalTime openAt;
    private LocalTime closeAt;

    private LocalDateTime createdAt;
    private Long createdBy;

    private LocalDateTime updatedAt;
    private Long updatedBy;

    public static StoreDetailsResponseDto from(Store store) {
        return StoreDetailsResponseDto.builder()
                .id(store.getId())
                .name(store.getName())
                .userId(store.getUser().getId())
                .userName(store.getUser().getName())
                .userNickname(store.getUser().getNickname())
                .categoryCode(store.getCategory().getCode())
                .categoryName(store.getCategory().getName())
                .status(store.getStatus())
                .isHidden(store.getIsHidden())
                .averageRating(store.getAverageRating())
                .reviewCount(store.getReviewCount())
                .addressName(store.getAddressName())
                .region1depthName(store.getRegion1depthName())
                .region2depthName(store.getRegion2depthName())
                .region3depthName(store.getRegion3depthName())
                .detailAddress(store.getDetailAddress())
                .longitude(store.getLongitude())
                .latitude(store.getLatitude())
                .comment(store.getComment())
                .openAt(store.getOpenAt())
                .closeAt(store.getCloseAt())
                .createdAt(store.getCreatedAt())
                .createdBy(store.getCreatedBy())
                .updatedAt(store.getUpdatedAt())
                .updatedBy(store.getUpdatedBy())
                .build();
    }
}
