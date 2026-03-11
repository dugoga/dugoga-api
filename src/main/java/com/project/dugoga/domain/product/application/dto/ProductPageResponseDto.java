package com.project.dugoga.domain.product.application.dto;

import com.project.dugoga.domain.product.domain.model.entity.Product;
import com.project.dugoga.global.dto.PageInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class ProductPageResponseDto {
    private List<ProductDetailDto> content;
    private PageInfoDto pageInfo;

    public static ProductPageResponseDto from(Page<Product> page){
        return ProductPageResponseDto.builder()
                .content(page.getContent().stream()
                        .map(ProductDetailDto::from)
                        .toList())
                .pageInfo(PageInfoDto.from(page))
                .build();
    }

    @Getter
    @Builder
    public static class ProductDetailDto {
        private UUID id;
        private String name;
        private Integer price;
        private String imageUrl;
        private Boolean isSoldOut;
        private Boolean isHidden;

        public static ProductDetailDto from(Product product) {
            return ProductDetailDto.builder()
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
