package com.project.dugoga.domain.payment.infrastructure.repository;

import com.project.dugoga.domain.payment.domain.model.entity.PaymentHistory;
import com.project.dugoga.domain.payment.domain.repository.PaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentHistoryRepositoryImpl implements PaymentHistoryRepository {

    private final PaymentHistoryJpaRepository paymentHistoryJpaRepository;

    @Override
    public PaymentHistory save(PaymentHistory paymentHistory) {
        return paymentHistoryJpaRepository.save(paymentHistory);
    }
}
