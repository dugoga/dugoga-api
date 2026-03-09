package com.project.dugoga.domain.payment.application.pg;

import com.project.dugoga.domain.payment.application.dto.PGConfirmResult;

import java.util.UUID;

public interface PGClient {
    PGConfirmResult confirm(String paymentKey, UUID orderId, Integer amount);
}
