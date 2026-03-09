package com.project.dugoga.domain.payment.application.dto;

import com.project.dugoga.domain.payment.domain.model.entity.Payment;
import com.project.dugoga.domain.payment.domain.model.enums.PaymentMethod;
import com.project.dugoga.domain.payment.domain.model.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class UserPaymentDetailResponseDto {

    private UUID paymentId;
    private UUID orderId;
    private Integer price;
    private PaymentMethod method;
    private PaymentStatus status;
    private LocalDateTime approvedAt;

    public static UserPaymentDetailResponseDto from(Payment payment) {
        return UserPaymentDetailResponseDto.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrder().getId())
                .price(payment.getPrice())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .approvedAt(payment.getCreatedAt())
                .build();
    }
}
