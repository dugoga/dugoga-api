package com.project.dugoga.domain.product.infrastructure.repository;

import com.project.dugoga.domain.product.domain.model.entity.Product;
import com.project.dugoga.domain.product.domain.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepositoryImpl extends JpaRepository<Product, UUID>, ProductRepository {
    List<Product> findAllByStoreIdAndIdInAndDeletedAtIsNull(UUID storeId, Collection<UUID> ids);

    Optional<Product> findByIdAndDeletedAtIsNull(UUID id);

    Page<Product> findByStoreIdAndIsHiddenFalse(UUID storeId, Pageable pageable);

    Page<Product> findByStoreId(UUID storeId, Pageable pageable);

    Page<Product> findByStoreIdAndNameContaining(UUID storeId, String name, Pageable pageable);

    Page<Product> findByStoreIdAndNameContainingAndIsHiddenFalse(UUID storeId, String name, Pageable pageable);

    Page<Product> findByNameContaining(String name, Pageable pageable);

    Page<Product> findByIsHiddenFalse(Pageable pageable);

    Page<Product> findByNameContainingAndIsHiddenFalse(String name, Pageable pageable);

    @Query("select p from Product  p join fetch  p.store where p.id = :productId and p.deletedAt = null")
    Optional<Product> findByIdWithStoreAndDeletedAtIsNull(@Param("productId") UUID productId);

    @Query("SELECT p FROM Product p JOIN FETCH p.store WHERE p.id IN :productIds and p.deletedAt = null")
    List<Product> findAllByIdInWithStoreAndDeletedAtIsNull(@Param("productIds") List<UUID> productIds);
}
