package com.project.dugoga.domain.store.application.dto;

import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.global.dto.PageInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StorePageResponseDto {

    private List<StoreItemDto> stores;
    private PageInfoDto pageInfo;

    public static StorePageResponseDto from(Page<Store> storePage) {
        return StorePageResponseDto.builder()
                .stores(storePage.getContent().stream()
                        .map(StoreItemDto::from)
                        .toList())
                .pageInfo(PageInfoDto.from(storePage))
                .build();
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StoreItemDto {
        private UUID id;
        private String name;
        private String status;
        private UUID categoryId;
        private String categoryCode;
        private String categoryName;
        private Double averageRating;
        private Long reviewCount;
        private Boolean isHidden;

        public static StoreItemDto from(Store store) {
            return StoreItemDto.builder()
                    .id(store.getId())
                    .name(store.getName())
                    .status(store.getStatus().name())
                    .categoryId(store.getCategory().getId())
                    .categoryCode(store.getCategory().getCode())
                    .categoryName(store.getCategory().getName())
                    .averageRating(store.getAverageRating())
                    .reviewCount(store.getReviewCount())
                    .isHidden(store.getIsHidden())
                    .build();
        }
    }
}
