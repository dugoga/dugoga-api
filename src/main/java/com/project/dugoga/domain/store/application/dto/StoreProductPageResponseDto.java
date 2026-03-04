package com.project.dugoga.domain.store.application.dto;

import com.project.dugoga.domain.product.domain.model.entity.Product;
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
public class StoreProductPageResponseDto {
    private List<ProductDto> products;
    private PageInfoDto pageInfo;

    public static StoreProductPageResponseDto from(Page<Product> productPage) {
        return StoreProductPageResponseDto.builder()
                .products(productPage.getContent().stream()
                        .map(ProductDto::from)
                        .toList())
                .pageInfo(PageInfoDto.from(productPage))
                .build();
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductDto {
        private UUID id;
        private String name;
        private Integer price;
        private String imageUrl;
        private Boolean isSoldOut;
        private Boolean isHidden;

        public static ProductDto from(Product product) {
            return ProductDto.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .imageUrl(product.getImageUrl())
                    .isSoldOut(product.getIsSoldOut())
                    .isHidden(product.getIsHidden())
                    .build();
        }
    }
}
