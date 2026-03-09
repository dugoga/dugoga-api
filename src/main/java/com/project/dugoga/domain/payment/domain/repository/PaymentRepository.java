package com.project.dugoga.domain.payment.domain.repository;

import com.project.dugoga.domain.payment.application.dto.UserPaymentListResponseDto;
import com.project.dugoga.domain.payment.domain.model.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PaymentRepository {
    Payment save(Payment payment);

    Boolean existsByOrder_Id(UUID orderId);

    Page<Payment> searchPayments(Long userId, String keywork, Pageable pageable);
}
