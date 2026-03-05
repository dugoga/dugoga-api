package com.project.dugoga.domain.store.domain.repository;

import com.project.dugoga.domain.store.domain.model.entity.Store;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface StoreRepository {

    Store save(Store store);
    Optional<Store> findById(UUID storeId);
    Set<Store> findByIdIn(Collection<UUID> storeIds);
    Optional<Store> findByIdWithDetails(UUID storeId);

    // MASTER, MANAGER
    Page<Store> findAll(Pageable pageable);
    Page<Store> findByNameContaining(String name, Pageable pageable);

    // CUSTOMER, OWNER
    Page<Store> findByIsHiddenFalse(Pageable pageable);
    Page<Store> findByNameContainingAndIsHiddenFalse(String name, Pageable pageable);
}
