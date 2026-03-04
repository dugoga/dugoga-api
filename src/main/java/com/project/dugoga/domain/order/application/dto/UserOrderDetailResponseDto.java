package com.project.dugoga.domain.order.application.dto;

import com.project.dugoga.domain.order.domain.model.entity.Order;
import com.project.dugoga.domain.order.domain.model.entity.OrderProduct;
import com.project.dugoga.domain.order.domain.model.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class UserOrderDetailResponseDto {

    private UUID id;
    private Long userId;
    private String requestMessage;
    private OrderStatus status;
    private Integer amount;
    private Integer deliverFee;
    private Integer totalAmount;
    private LocalDateTime orderedAt;
    private String storeName;
    private List<OrderProductResponse> orderProducts;

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

    public static UserOrderDetailResponseDto from (Order order) {
        List<OrderProductResponse> orderProducts = order.getOrderProducts().stream()
                .map(OrderProductResponse::from)
                .toList();

        return UserOrderDetailResponseDto.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .requestMessage(order.getRequestMessage())
                .status(order.getStatus())
                .amount(order.getAmount())
                .deliverFee(order.getDeliveryFee())
                .totalAmount(order.getTotalAmount())
                .orderedAt(order.getCreatedAt())
                .storeName(order.getStore().getName())
                .orderProducts(orderProducts)
                .build();
    }
}
