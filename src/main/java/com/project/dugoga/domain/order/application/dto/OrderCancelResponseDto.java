package com.project.dugoga.domain.order.application.dto;

import com.project.dugoga.domain.order.domain.model.entity.Order;
import com.project.dugoga.domain.order.domain.model.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class OrderCancelResponseDto {

    private UUID orderId;
    private OrderStatus status;
    private LocalDateTime canceledAt;
    private UUID storeId;

    public static OrderCancelResponseDto from (Order order) {
        return OrderCancelResponseDto.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .canceledAt(order.getUpdatedAt())
                .storeId(order.getStore().getId())
                .build();
    }
}
