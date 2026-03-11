package com.project.dugoga.domain.order.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class OrderCreateRequestDto {

    @NotNull
    private UUID storeId;

    @NotEmpty(message = "주문할 상품을 한 개 이상 선택해 주세요.")
    private List<Product> products;

    @Size(max = 255, message = "요청 메시지는 최대 255자까지 입력할 수 있습니다.")
    private String requestMessage;


    @Getter
    @AllArgsConstructor
    public static class Product {

        @NotNull(message = "상품 ID는 필수입니다.")
        private UUID id;

        @NotNull(message = "상품 수량은 필수입니다.")
        @Min(value = 1, message = "상품 수량은 최소 1개 이상이어야 합니다.")
        private Integer quantity;
    }
}
