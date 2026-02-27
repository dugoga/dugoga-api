package com.project.dugoga.domain.order.repository;

import com.project.dugoga.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
}
