package com.project.dugoga.domain.payment.infrastructure.repository;

import com.project.dugoga.domain.payment.domain.model.entity.Payment;
import com.project.dugoga.domain.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;
    private final PaymentCustomRepository paymentCustomRepository;

    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(payment);
    }

    @Override
    public Boolean existsByOrder_Id(UUID orderId) {
        return paymentJpaRepository.existsByOrder_Id(orderId);
    }

    @Override
    public Page<Payment> searchPayments(Long userId, String keyword, Pageable pageable) {
        return paymentCustomRepository.searchPayment(userId, keyword, pageable);
    }

    @Override
    public Optional<Payment> findPayment(UUID paymentId, Long userId) {
        return paymentJpaRepository.findByIdAndUser_IdAndDeletedAtIsNull(paymentId, userId);
    }

    @Override
    public Optional<Payment> findByOrder_Id(UUID orderId) {
        return paymentJpaRepository.findByOrder_Id(orderId);
    }
}
