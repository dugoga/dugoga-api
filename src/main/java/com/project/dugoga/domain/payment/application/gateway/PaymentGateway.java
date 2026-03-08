package com.project.dugoga.domain.payment.application.gateway;

import com.project.dugoga.domain.payment.application.dto.PaymentGatewayConfirmResult;

import java.util.UUID;

public interface PaymentGateway {
    PaymentGatewayConfirmResult confirm(String paymentKey, UUID orderId, Integer amount);
}
