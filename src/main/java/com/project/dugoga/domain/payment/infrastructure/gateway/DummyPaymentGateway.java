package com.project.dugoga.domain.payment.infrastructure.gateway;

import com.project.dugoga.domain.payment.application.dto.PaymentGatewayConfirmResult;
import com.project.dugoga.domain.payment.application.gateway.PaymentGateway;
import com.project.dugoga.domain.payment.domain.model.enums.PaymentMethod;
import com.project.dugoga.domain.payment.domain.model.enums.PaymentStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class DummyPaymentGateway implements PaymentGateway {

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
