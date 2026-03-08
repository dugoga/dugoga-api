package com.project.dugoga.domain.payment.application.pg;

import com.project.dugoga.domain.payment.application.dto.PaymentGatewayConfirmResult;

import java.util.UUID;

public interface PGClient {
    PaymentGatewayConfirmResult confirm(String paymentKey, UUID orderId, Integer amount);
}
