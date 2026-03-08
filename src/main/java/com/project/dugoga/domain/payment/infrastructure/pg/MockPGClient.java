package com.project.dugoga.domain.payment.infrastructure.pg;

import com.project.dugoga.domain.payment.application.dto.PaymentGatewayConfirmResult;
import com.project.dugoga.domain.payment.application.pg.PGClient;
import com.project.dugoga.domain.payment.domain.model.enums.PaymentMethod;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class MockPGClient implements PGClient {

    @Override
    public PaymentGatewayConfirmResult confirm(String paymentKey, UUID orderId, Integer amount) {
        return PaymentGatewayConfirmResult.builder()
                .paymentKey(paymentKey)
                .orderId(orderId)
                .method(PaymentMethod.CARD)
                .totalAmount(amount)
                .status("DONE")
                .requestedAt(LocalDateTime.now().minusMinutes(5))
                .approvedAt(LocalDateTime.now())
                .build();
    }
}
