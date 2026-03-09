package com.project.dugoga.domain.payment.infrastructure.repository;

import com.project.dugoga.domain.payment.domain.model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentJpaRepository extends JpaRepository<Payment, UUID> {
    boolean existsByOrder_Id(UUID orderId);
}
