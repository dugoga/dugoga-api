package com.project.dugoga.domain.order.application.dto;

import com.project.dugoga.domain.order.domain.model.entity.Order;
import com.project.dugoga.domain.order.domain.model.entity.OrderProduct;
import com.project.dugoga.domain.order.domain.model.enums.OrderStatus;
import com.project.dugoga.global.dto.PageInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class UserOrderListResponseDto {

    private List<OrderResponse> orders;
    private PageInfoDto pageInfo;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class OrderResponse {
        private UUID id;
        private Long userId;
        private OrderStatus status;
        private Integer totalAmount;
        private LocalDateTime orderedAt;
        private String storeName;
        private List<OrderProductResponse> orderProducts;

        public static OrderResponse from(Order order, List<OrderProduct> orderProducts) {
            List<OrderProductResponse> list = orderProducts.stream()
                    .map(OrderProductResponse::from)
                    .toList();

            return OrderResponse.builder()
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

        public static OrderProductResponse from (OrderProduct item) {
            return OrderProductResponse.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .build();
        }
    }

    public static UserOrderListResponseDto of (List<OrderResponse> orders, PageInfoDto pageInfo) {
        return UserOrderListResponseDto.builder()
                .orders(orders)
                .pageInfo(pageInfo)
                .build();
    }
}
