package com.project.dugoga.domain.payment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "p_payment_history")
public class PaymentHistory {

    @Id
    private UUID id;
}
