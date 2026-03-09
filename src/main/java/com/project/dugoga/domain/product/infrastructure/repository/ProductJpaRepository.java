package com.project.dugoga.domain.product.infrastructure.repository;

import com.project.dugoga.domain.product.domain.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductJpaRepository extends JpaRepository<Product, UUID> {
    List<Product> findAllByStoreIdAndIdInAndDeletedAtIsNull(UUID storeId, Collection<UUID> ids);

    Optional<Product> findByIdAndDeletedAtIsNull(UUID id);

    @Query("select p from Product  p join fetch  p.store join fetch p.store.user where p.id = :productId and p.deletedAt is null")
    Optional<Product> findByIdWithStoreAndDeletedAtIsNull(@Param("productId") UUID productId);

    @Query("SELECT p FROM Product p JOIN FETCH p.store WHERE p.id IN :productIds and p.deletedAt is null")
    List<Product> findAllByIdInWithStoreAndDeletedAtIsNull(@Param("productIds") List<UUID> productIds);
}
