package com.project.dugoga.domain.store.domain.repository;

import com.project.dugoga.domain.store.domain.model.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;

public interface StoreRepository {

    Store save(Store store);
    Optional<Store> findByIdAndDeletedAtIsNull(UUID storeId);
    List<Store> findByIdInAndDeletedAtIsNull(Collection<UUID> storeIds);
    Optional<Store> findByIdWithDetailsAndDeletedAtIsNull(UUID storeId);

    // MASTER, MANAGER
    Page<Store> findAll(Pageable pageable);
    Page<Store> findByNameContaining(String name, Pageable pageable);

    // CUSTOMER, OWNER
    Page<Store> findByIsHiddenFalse(Pageable pageable);
    Page<Store> findByNameContainingAndIsHiddenFalse(String name, Pageable pageable);
}
