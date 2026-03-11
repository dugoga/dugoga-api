package com.project.dugoga.domain.order.domain.repository;

import com.project.dugoga.domain.order.domain.model.entity.OrderProduct;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface OrderProductRepository {
    List<OrderProduct> findAllByOrder_IdIn(Collection<UUID> orderIds);
}
