package com.project.dugoga.domain.product.application.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductVisibilityUpdateRequestDto {

    @NotEmpty(message = "최소 한 개 이상의 상품 ID를 입력해야 합니다.")
    @Size(max = 100, message = "한 번에 최대 100개까지만 처리할 수 있습니다.")
    List<UUID> productIds;

    @NotNull(message = "숨김 여부는 필수입니다.")
    Boolean isHidden;
}
