package com.project.dugoga.domain.payment.domain.repository;

import com.project.dugoga.domain.payment.domain.model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}
