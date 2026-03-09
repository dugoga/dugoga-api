package com.project.dugoga.domain.payment.infrastructure.repository;

import com.project.dugoga.domain.payment.domain.model.entity.Payment;
import com.project.dugoga.domain.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(payment);
    }

    @Override
    public Boolean existsByOrder_Id(UUID orderId) {
        return paymentJpaRepository.existsByOrder_Id(orderId);
    }
}
