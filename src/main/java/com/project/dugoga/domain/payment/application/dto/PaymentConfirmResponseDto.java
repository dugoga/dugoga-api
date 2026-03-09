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
public class PaymentConfirmResponseDto {

    private UUID paymentId;
    private UUID orderId;
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
    private Integer totalAmount;
    private LocalDateTime confirmedAt;

    public static PaymentConfirmResponseDto from (Payment payment) {
        return PaymentConfirmResponseDto.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrder().getId())
                .status(payment.getStatus())
                .paymentMethod(payment.getMethod())
                .totalAmount(payment.getPrice())
                .confirmedAt(payment.getUpdatedAt())
                .build();
    }
}
