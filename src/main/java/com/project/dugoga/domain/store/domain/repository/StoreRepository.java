package com.project.dugoga.domain.store.domain.repository;

import com.project.dugoga.domain.store.domain.model.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoreRepository {

    Store save(Store store);
    Optional<Store> findByIdAndDeletedAtIsNull(UUID storeId);
    List<Store> findByIdInAndDeletedAtIsNull(Collection<UUID> storeIds);
    Optional<Store> findByIdWithDetailsAndDeletedAtIsNull(UUID storeId);

    /*
        isAdmin = MANAGER, MASTER -> true
     */
    Page<Store> searchStores(String keyword, String category, Long userId, boolean isAdmin, Pageable pageable);
}
