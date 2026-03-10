package com.project.dugoga.config.generator;

import com.project.dugoga.domain.order.domain.model.entity.Order;
import com.project.dugoga.domain.order.domain.model.enums.OrderStatus;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.user.domain.model.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

public class OrderFixtureGenerator {

    public static final String REQUEST_MESSAGE = "문 앞에 두고 가주세요.";
    public static final Integer AMOUNT = 20000;
    public static final Integer DELIVERY_FEE = 5000;

    public static Order generateOrderFixture(User user, Store store) {
        return Order.create(
                user,
                store,
                REQUEST_MESSAGE,
                AMOUNT,
                DELIVERY_FEE
        );
    }

    // 커스텀용
    public static Order generateOrderFixture(
            User user, Store store, String requestMessage,
            Integer amount, Integer deliveryFee)
    {
        return Order.create(
                user,
                store,
                requestMessage,
                amount,
                deliveryFee
        );
    }

    // 상태값, createdAt 설정 가능
    public static Order generateOrderFixture(
            User user, Store store, OrderStatus status, LocalDateTime createdAt
    ) {
        return Order.of(
                user,
                store,
                status,
                createdAt
        );
    }
}
