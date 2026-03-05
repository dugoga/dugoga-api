package com.project.dugoga.domain.product.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateRequestDto {

    // TODO: Principal 도입시 삭제해야 합니다.
    @NotNull(message = "회원 ID는 필수입니다.")
    private Long userId;

    @NotNull(message = "가게 ID는 필수입니다.")
    private UUID storeId;

    @NotBlank(message = "상품명은 필수입니다.")
    private String name;

    private String comment;

    @NotNull(message = "가격은 필수입니다.")
    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private Integer price;

    private String imageUrl;
}
