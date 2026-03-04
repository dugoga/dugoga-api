package com.project.dugoga.domain.order.domain.model.entity;

import com.project.dugoga.domain.order.domain.model.enums.OrderStatus;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.global.entity.BaseEntity;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_order")
public class Order extends BaseEntity {

    private static final long CANCELABLE_MINUTE = 5L;

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

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    private List<OrderProduct> orderProducts = new ArrayList<>();

    @Builder(access = AccessLevel.PRIVATE)
    private Order(User user, Store store, String requestMessage, OrderStatus status, Integer amount,
                  Integer deliveryFee, Integer totalAmount) {
        this.user = user;
        this.store = store;
        this.requestMessage = requestMessage;
        this.status = status;
        this.amount = amount;
        this.deliveryFee = deliveryFee;
        this.totalAmount = totalAmount;
    }

    public static Order create(
            User user,
            Store store,
            String requestMessage,
            Integer amount,
            Integer deliveryFee
    ) {
        return Order.builder()
                .user(user)
                .store(store)
                .requestMessage(requestMessage)
                .status(OrderStatus.CREATED)
                .amount(amount)
                .deliveryFee(deliveryFee)
                .totalAmount(amount + deliveryFee)
                .build();
    }

    public void addOrderProducts(List<OrderProduct> items) {
        this.orderProducts.addAll(items);
    }

    private void validateCancelable() {
        if (this.status == OrderStatus.CANCELED) {
            throw new BusinessException(ErrorCode.ORDER_ALREADY_CANCELLED);
        }

        if (this.status != OrderStatus.CREATED) {
            throw new BusinessException(ErrorCode.ORDER_CANCEL_NOT_ALLOWED_STATUS);
        }

        // 5분 안에만 취소 가능
        if (this.getCreatedAt().plusMinutes(CANCELABLE_MINUTE).isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.ORDER_CANCEL_TIME_EXPIRED);
        }
    }

    public void cancel() {
        validateCancelable();
        this.status = OrderStatus.CANCELED;
    }
}
