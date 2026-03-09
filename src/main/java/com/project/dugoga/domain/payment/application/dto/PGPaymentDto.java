package com.project.dugoga.domain.payment.application.dto;

import com.project.dugoga.domain.payment.domain.model.enums.PaymentMethod;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class PGPaymentDto {
    private String paymentKey;
    private UUID orderId;

    private PaymentMethod method;
    private Integer totalAmount;

    private String status;

    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;

    private List<PGCancelDto> cancels;
}
