package com.project.dugoga.domain.payment.infrastructure.repository;

import com.project.dugoga.domain.payment.domain.model.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentHistoryJpaRepository extends JpaRepository<PaymentHistory, UUID> {
}
