package com.project.dugoga.domain.order.infrastructure.repository;

import com.project.dugoga.domain.order.domain.model.entity.Order;
import com.project.dugoga.domain.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Page<Order> findAllWithStoreByUser_IdAndDeletedAtIsNull(Long userId, Pageable pageable) {
        return orderJpaRepository.findAllWithStoreByUser_IdAndDeletedAtIsNull(userId, pageable);
    }

    @Override
    public Page<Order> findAllWithStoreByUser_IdAndStore_NameContainingIgnoreCaseAndDeletedAtIsNull(Long userId, String storeName, Pageable pageable) {
        return orderJpaRepository.findAllWithStoreByUser_IdAndStore_NameContainingIgnoreCaseAndDeletedAtIsNull(userId, storeName, pageable);
    }

    @Override
    public Page<Order> findAllWithStoreByStore_IdAndDeletedAtIsNull(UUID storeId, Pageable pageable) {
        return orderJpaRepository.findAllWithStoreByStore_IdAndDeletedAtIsNull(storeId, pageable);
    }

    @Override
    public Page<Order> findAllWithStoreByStore_IdAndStore_NameContainingIgnoreCaseAndDeletedAtIsNull(UUID storeId, String storeName, Pageable pageable) {
        return orderJpaRepository.findAllWithStoreByStore_IdAndStore_NameContainingIgnoreCaseAndDeletedAtIsNull(storeId, storeName, pageable);
    }

    @Override
    public Optional<Order> findWithStoreAndOrderProductsByIdAndUser_IdAndDeletedAtIsNull(UUID id, Long userId) {
        return orderJpaRepository.findWithStoreAndOrderProductsByIdAndUser_IdAndDeletedAtIsNull(id, userId);
    }

    @Override
    public Optional<Order> findWithStoreByIdAndDeletedAtIsNull(UUID id) {
        return orderJpaRepository.findWithStoreByIdAndDeletedAtIsNull(id);
    }

    @Override
    public Optional<Order> findByIdAndUser_IdAndDeletedAtIsNull(UUID id, Long userId) {
        return orderJpaRepository.findByIdAndUser_IdAndDeletedAtIsNull(id, userId);
    }

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }
}
