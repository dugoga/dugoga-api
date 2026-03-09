package com.project.dugoga.domain.payment.domain.model.entity;

import com.project.dugoga.domain.order.domain.model.entity.Order;
import com.project.dugoga.domain.payment.domain.model.enums.PaymentMethod;
import com.project.dugoga.domain.payment.domain.model.enums.PaymentStatus;
import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_payment")
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(nullable = false)
    private Integer price = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Column(name = "payment_key", length = 255)
    private String paymentKey;

    @OneToMany(mappedBy = "payment")
    private List<PaymentHistory> paymentHistories = new ArrayList<>();

    @Builder(access = AccessLevel.PRIVATE)
    private Payment(User user, Order order, Integer price, PaymentStatus status,
                    PaymentMethod method, String paymentKey) {
        this.user = user;
        this.order = order;
        this.price = price;
        this.status = status;
        this.method = method;
        this.paymentKey = paymentKey;
    }

    public static Payment create(Order order, PaymentMethod method, String paymentKey) {
        return Payment.builder()
                .user(order.getUser())
                .order(order)
                .price(order.getTotalAmount())
                .method(method)
                .paymentKey(paymentKey)
                .status(PaymentStatus.READY)
                .build();
    }

    public void updateStatus(PaymentStatus status) {
        this.status = status;
    }

}
