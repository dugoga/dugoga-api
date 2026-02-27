package com.project.dugoga.domain.order.domain.repository;

import com.project.dugoga.domain.order.domain.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
}
