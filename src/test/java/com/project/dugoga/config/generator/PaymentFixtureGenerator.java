package com.project.dugoga.config.generator;

import com.project.dugoga.domain.order.domain.model.entity.Order;
import com.project.dugoga.domain.payment.domain.model.entity.Payment;
import com.project.dugoga.domain.payment.domain.model.enums.PaymentStatus;
import com.project.dugoga.domain.user.domain.model.entity.User;

import java.util.UUID;

public class PaymentFixtureGenerator {

    public static Payment generatePaymentFixture(
            User user,
            Order order,
            String paymentKey,
            PaymentStatus status
    ) {
        return Payment.builder()
                .user(user)
                .order(order)
                .paymentKey(paymentKey)
                .status(status)
                .price(15000)
                .build();
    }
}
