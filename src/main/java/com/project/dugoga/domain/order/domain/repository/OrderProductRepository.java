package com.project.dugoga.domain.order.domain.repository;

import com.project.dugoga.domain.order.domain.model.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderProductRepository extends JpaRepository<OrderProduct, UUID> {
}
