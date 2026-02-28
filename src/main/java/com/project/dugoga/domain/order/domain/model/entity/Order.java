package com.project.dugoga.domain.order.domain.model.entity;

import com.project.dugoga.domain.order.domain.model.enums.OrderStatus;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.user.domain.model.entity.User;
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
@Table(name = "p_order")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "request_message", length = 255)
    private String requestMessage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private Integer amount = 0;

    @Column(name = "delivery_fee", nullable = false)
    private Integer deliveryFee = 0;

    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount = 0;


    @Builder(access = AccessLevel.PRIVATE)
    private Order(String requestMessage, OrderStatus status, Integer amount,
                  Integer deliveryFee, Integer totalAmount) {
        this.requestMessage = requestMessage;
        this.status = status;
        this.amount = amount;
        this.deliveryFee = deliveryFee;
        this.totalAmount = totalAmount;
    }

}
