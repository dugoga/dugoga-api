package com.project.dugoga.domain.payment.domain.repository;

import com.project.dugoga.domain.payment.domain.model.entity.PaymentHistory;

public interface PaymentHistoryRepository {
    PaymentHistory save(PaymentHistory paymentHistory);
}
