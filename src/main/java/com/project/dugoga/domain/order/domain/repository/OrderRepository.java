package com.project.dugoga.domain.order.domain.repository;

import com.project.dugoga.domain.order.domain.model.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Page<Order> findAllWithStoreByUser_IdAndDeletedAtIsNull(Long userId, Pageable pageable);

    Page<Order> findAllWithStoreByUser_IdAndStore_NameContainingIgnoreCaseAndDeletedAtIsNull(Long userId, String storeName, Pageable pageable);

    Page<Order> findAllWithStoreByStore_IdAndDeletedAtIsNull(UUID storeId, Pageable pageable);

    Page<Order> findAllWithStoreByStore_IdAndStore_NameContainingIgnoreCaseAndDeletedAtIsNull(UUID storeId, String storeName, Pageable pageable);

    Optional<Order> findWithStoreAndOrderProductsByIdAndUser_IdAndDeletedAtIsNull(UUID id, Long userId);

    Optional<Order> findWithStoreByIdAndDeletedAtIsNull(UUID id);

    Optional<Order> findByIdAndUser_IdAndDeletedAtIsNull(UUID id, Long userId);

    Order save(Order order);
}
