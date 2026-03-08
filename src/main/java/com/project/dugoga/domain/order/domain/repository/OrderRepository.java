package com.project.dugoga.domain.order.domain.repository;

import com.project.dugoga.domain.order.domain.model.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    @EntityGraph(attributePaths = {"store"})
    Page<Order> findAllWithStoreByUser_IdAndDeletedAtIsNull(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"store"})
    Page<Order> findAllWithStoreByUser_IdAndStore_NameContainingIgnoreCaseAndDeletedAtIsNull(Long userId, String storeName, Pageable pageable);

    @EntityGraph(attributePaths = {"store"})
    Page<Order> findAllWithStoreByStore_IdAndDeletedAtIsNull(UUID storeId, Pageable pageable);

    @EntityGraph(attributePaths = {"store"})
    Page<Order> findAllWithStoreByStore_IdAndStore_NameContainingIgnoreCaseAndDeletedAtIsNull(UUID storeId, String storeName, Pageable pageable);

    @EntityGraph(attributePaths = {"store", "orderProducts"})
    Optional<Order> findWithStoreAndOrderProductsByIdAndUser_IdAndDeletedAtIsNull(UUID id, Long userId);

    @EntityGraph(attributePaths = {"store"})
    Optional<Order> findWithStoreByIdAndDeletedAtIsNull(UUID id);
}
