package com.project.dugoga.domain.order.application.dto;

import com.project.dugoga.domain.order.domain.model.entity.Order;
import com.project.dugoga.domain.order.domain.model.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class OrderAcceptResponseDto {

    private UUID orderId;
    private OrderStatus status;
    private LocalDateTime acceptedAt;
    private UUID storeId;

    public static OrderAcceptResponseDto from (Order order) {
        return OrderAcceptResponseDto.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .acceptedAt(order.getUpdatedAt())
                .storeId(order.getStore().getId())
                .build();
    }
}
