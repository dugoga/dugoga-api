package com.project.dugoga.domain.order.domain.model.entity;

import com.project.dugoga.domain.product.domain.model.entity.Product;
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
@Table(
        name = "p_order_product",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_p_order_product_order_product",
                        columnNames = {"order_id", "product_id"}
                )
        }
)
public class OrderProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false)
    private Integer quantity = 0;

    @Column(nullable = false)
    private Integer price = 0;

    @Builder(access = AccessLevel.PRIVATE)
    private OrderProduct(Order order, Product product, String name, Integer quantity, Integer price) {
        this.order = order;
        this.product = product;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public static OrderProduct create(Order order, Product product, String name, Integer quantity, Integer price) {
        return OrderProduct.builder()
                .order(order)
                .product(product)
                .name(name)
                .quantity(quantity)
                .price(price)
                .build();
    }
}
