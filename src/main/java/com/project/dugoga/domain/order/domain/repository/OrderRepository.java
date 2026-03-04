package com.project.dugoga.domain.order.domain.repository;

import com.project.dugoga.domain.order.domain.model.entity.Order;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.user.domain.model.entity.User;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    @EntityGraph(attributePaths = {"store"})
    Page<Order> findAllByUser(User user, Pageable pageable);

    @EntityGraph(attributePaths = {"store"})
    Page<Order> findAllByUserAndStore_NameContainingIgnoreCase(User user, String storeName, Pageable pageable);

    @EntityGraph(attributePaths = {"store"})
    Page<Order> findAllByStore(Store store, Pageable pageable);

    @EntityGraph(attributePaths = {"store"})
    Page<Order> findAllByStoreAndStore_NameContainingIgnoreCase(Store store, String storeName, Pageable pageable);
}
