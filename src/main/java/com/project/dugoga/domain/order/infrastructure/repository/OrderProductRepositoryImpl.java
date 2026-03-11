package com.project.dugoga.domain.order.infrastructure.repository;

import com.project.dugoga.domain.order.domain.model.entity.OrderProduct;
import com.project.dugoga.domain.order.domain.repository.OrderProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderProductRepositoryImpl implements OrderProductRepository {

    private final OrderProductJpaRepository orderProductJpaRepository;

    @Override
    public List<OrderProduct> findAllByOrder_IdIn(Collection<UUID> orderIds) {
        return orderProductJpaRepository.findAllByOrder_IdIn(orderIds);
    }
}
