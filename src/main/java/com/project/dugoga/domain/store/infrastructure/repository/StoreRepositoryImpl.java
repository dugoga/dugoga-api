package com.project.dugoga.domain.store.infrastructure.repository;

import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.store.domain.repository.StoreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface StoreRepositoryImpl extends JpaRepository<Store, UUID>, StoreRepository {

    @Query("select s from Store s join fetch s.user join fetch s.category where s.id = :storeId and s.deletedAt = null")
    Optional<Store> findByIdWithDetailsAndDeletedAtIsNull(@Param("stoerId") UUID storeId);

    Optional<Store> findByIdAndDeletedAtIsNull(UUID storeId);

    List<Store> findByIdInAndDeletedAtIsNull(Collection<UUID> storeIds);

    Page<Store> findByNameContaining(String name, Pageable pageable);

    Page<Store> findByIsHiddenFalse(Pageable pageable);

    Page<Store> findByNameContainingAndIsHiddenFalse(String name, Pageable pageable);
}
