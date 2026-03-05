package com.project.dugoga.domain.order.application.dto;

import com.project.dugoga.domain.order.domain.model.entity.Order;
import com.project.dugoga.domain.order.domain.model.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class OrderRejectResponseDto {

    private UUID orderId;
    private OrderStatus status;
    private LocalDateTime rejectedAt;
    private UUID storeId;

    public static OrderRejectResponseDto from (Order order) {
        return OrderRejectResponseDto.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .rejectedAt(order.getUpdatedAt())
                .storeId(order.getStore().getId())
                .build();
    }
}
