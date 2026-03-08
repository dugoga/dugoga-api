package com.project.dugoga.domain.payment.domain.repository;

import com.project.dugoga.domain.payment.domain.model.entity.Payment;

import java.util.UUID;

public interface PaymentRepository {
    Payment save(Payment payment);
    Boolean existsByOrder_Id(UUID orderId);
}
