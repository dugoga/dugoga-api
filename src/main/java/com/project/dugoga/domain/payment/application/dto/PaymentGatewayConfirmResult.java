package com.project.dugoga.domain.payment.application.dto;

import com.project.dugoga.domain.payment.domain.model.enums.PaymentMethod;
import com.project.dugoga.domain.payment.domain.model.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class PaymentGatewayConfirmResult {
    private String paymentKey;
    private UUID orderId;

    private PaymentMethod method;
    private Integer totalAmount;

    private String status;

    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
}
