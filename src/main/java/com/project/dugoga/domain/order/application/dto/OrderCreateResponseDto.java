package com.project.dugoga.domain.order.application.dto;

import com.project.dugoga.domain.order.domain.model.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class OrderCreateResponseDto {

    private UUID id;
    private LocalDateTime createdAt;

    public static OrderCreateResponseDto from(Order order) {
        return OrderCreateResponseDto.builder()
                .id(order.getId())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
