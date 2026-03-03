package com.project.dugoga.domain.store.domain.repository;

import com.project.dugoga.domain.store.domain.model.entity.Store;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {

    @Query("select s from Store s left join fetch s.products where s.id = :storeId")
    Optional<Store> findByIdWithProducts(@Param("id") UUID storeId);

    @Query("select s from Store s join fetch s.user join fetch s.category where s.id = :storeId")
    Optional<Store> findByIdWithDetails(@Param("stoerId") UUID storeId);

    Set<Store> findByIdIn(List<UUID> storeIds);
}
