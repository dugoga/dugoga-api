package com.project.dugoga.domain.order.infrastructure.repository;

import com.project.dugoga.domain.order.domain.model.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface OrderProductJpaRepository extends JpaRepository<OrderProduct, UUID> {
    List<OrderProduct> findAllByOrder_IdIn(Collection<UUID> orderIds);
}
