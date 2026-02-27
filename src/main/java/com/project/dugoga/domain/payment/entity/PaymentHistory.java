package com.project.dugoga.domain.payment.entity;

import com.project.dugoga.domain.order.entity.Order;
import com.project.dugoga.domain.payment.enums.PaymentMethod;
import com.project.dugoga.domain.payment.enums.PaymentStatus;
import com.project.dugoga.domain.user.entity.User;
import com.project.dugoga.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_payment_history")
public class PaymentHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private Integer price = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", nullable = false)
    private PaymentStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_status", nullable = false)
    private PaymentStatus currentStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Column(name = "payment_key", length = 255)
    private String paymentKey;

    @Column(length = 255)
    private String reason;

    @Builder(access = AccessLevel.PRIVATE)
    private PaymentHistory(Payment payment, User user, Order order, Integer price, PaymentStatus previousStatus,
                           PaymentStatus currentStatus, PaymentMethod method, String paymentKey, String reason) {
        this.payment = payment;
        this.user = user;
        this.order = order;
        this.price = price;
        this.previousStatus = previousStatus;
        this.currentStatus = currentStatus;
        this.method = method;
        this.paymentKey = paymentKey;
        this.reason = reason;
    }
}
