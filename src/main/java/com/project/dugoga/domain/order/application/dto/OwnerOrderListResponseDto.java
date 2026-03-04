package com.project.dugoga.domain.order.application.dto;

import com.project.dugoga.domain.order.domain.model.entity.Order;
import com.project.dugoga.domain.order.domain.model.entity.OrderProduct;
import com.project.dugoga.domain.order.domain.model.enums.OrderStatus;
import com.project.dugoga.global.dto.PageInfoDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class OwnerOrderListResponseDto {

    private List<OwnerOrderListResponseDto.OrderResponse> orders;
    private PageInfoDto pageInfo;

    @Getter
    @Builder
    public static class OrderResponse {
        private UUID id;
        private Long userId;
        private OrderStatus status;
        private Integer totalAmount;
        private LocalDateTime orderedAt;
        private String storeName;
        private List<OwnerOrderListResponseDto.OrderProductResponse> orderProducts;

        public static OwnerOrderListResponseDto.OrderResponse from(Order order, List<OrderProduct> orderProducts) {
            List<OwnerOrderListResponseDto.OrderProductResponse> list = orderProducts.stream()
                    .map(OwnerOrderListResponseDto.OrderProductResponse::from)
                    .toList();

            return OwnerOrderListResponseDto.OrderResponse.builder()
                    .id(order.getId())
                    .userId(order.getUser().getId())
                    .status(order.getStatus())
                    .totalAmount(order.getTotalAmount())
                    .orderedAt(order.getCreatedAt())
                    .storeName(order.getStore().getName())
                    .orderProducts(list)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class OrderProductResponse {
        private UUID id;
        private String name;
        private Integer quantity;
        private Integer price;

        public static OwnerOrderListResponseDto.OrderProductResponse from (OrderProduct item) {
            return OwnerOrderListResponseDto.OrderProductResponse.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .build();
        }
    }

    public static OwnerOrderListResponseDto of (List<OwnerOrderListResponseDto.OrderResponse> orders, PageInfoDto pageInfo) {
        return OwnerOrderListResponseDto.builder()
                .orders(orders)
                .pageInfo(pageInfo)
                .build();
    }
}
