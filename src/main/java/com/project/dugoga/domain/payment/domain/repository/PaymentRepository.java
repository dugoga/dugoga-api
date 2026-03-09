package com.project.dugoga.domain.payment.domain.repository;

import com.project.dugoga.domain.payment.domain.model.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {
    Payment save(Payment payment);

    Boolean existsByOrder_Id(UUID orderId);

    Page<Payment> searchPayments(Long userId, String keywork, Pageable pageable);

    Optional<Payment> findPayment(UUID paymentId, Long userId);
}
