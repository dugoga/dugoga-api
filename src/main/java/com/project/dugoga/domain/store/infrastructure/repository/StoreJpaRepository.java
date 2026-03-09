package com.project.dugoga.domain.store.infrastructure.repository;

import com.project.dugoga.domain.store.domain.model.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoreJpaRepository extends JpaRepository<Store, UUID> {

    @Query("select s from Store s join fetch s.user join fetch s.category where s.id = :storeId and s.deletedAt is null")
    Optional<Store> findByIdWithDetailsAndDeletedAtIsNull(@Param("storeId") UUID storeId);

    Optional<Store> findByIdAndDeletedAtIsNull(UUID storeId);

    List<Store> findByIdInAndDeletedAtIsNull(Collection<UUID> storeIds);

}
