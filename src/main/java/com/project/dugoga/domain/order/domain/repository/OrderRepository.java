package com.project.dugoga.domain.order.domain.repository;

import com.project.dugoga.domain.order.domain.model.entity.Order;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    @EntityGraph(attributePaths = {"store"})
    Page<Order> findAllByUser_Id(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"store"})
    Page<Order> findAllByUser_IdAndStore_NameContainingIgnoreCase(Long userId, String storeName, Pageable pageable);

    @EntityGraph(attributePaths = {"store"})
    Page<Order> findAllByStore_Id(UUID storeId, Pageable pageable);

    @EntityGraph(attributePaths = {"store"})
    Page<Order> findAllByStore_IdAndStore_NameContainingIgnoreCase(UUID storeId, String storeName, Pageable pageable);

    @EntityGraph(attributePaths = {"store", "orderProducts"})
    Optional<Order> findByIdAndUser_Id(UUID id, Long userId);

    @EntityGraph(attributePaths = {"store"})
    Optional<Order> findById(UUID id);
}
