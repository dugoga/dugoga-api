package com.project.dugoga.domain.payment.application.pg;

import com.project.dugoga.domain.payment.application.dto.PGPaymentDto;

import java.util.UUID;

public interface PGClient {
    PGPaymentDto confirm(String paymentKey, UUID orderId, Integer amount);

    PGPaymentDto cancel(String paymentKey, String reason);
}
