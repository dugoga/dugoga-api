package com.project.dugoga.domain.product.domain.repository;

import com.project.dugoga.domain.product.domain.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    Product save(Product product);
    Optional<Product> findByIdAndDeletedAtIsNull(UUID id);
    List<Product> findAllByStoreIdAndIdInAndDeletedAtIsNull(UUID storeId, Collection<UUID> ids);
    Optional<Product> findByIdWithStoreAndDeletedAtIsNull(UUID productId);
    List<Product> findAllByIdInWithStoreAndDeletedAtIsNull(List<UUID> productIds);

    // MASTER, MANAGER
    Page<Product> findByStoreId(UUID storeId, Pageable pageable);
    Page<Product> findByStoreIdAndNameContaining(UUID storeId, String name, Pageable pageable);
    Page<Product> findAll(Pageable pageable);
    Page<Product> findByNameContaining(String name, Pageable pageable);

    // CUSTOMER, OWNER
    Page<Product> findByStoreIdAndIsHiddenFalse(UUID storeId, Pageable pageable);
    Page<Product> findByStoreIdAndNameContainingAndIsHiddenFalse(UUID storeId, String name, Pageable pageable);
    Page<Product> findByIsHiddenFalse(Pageable pageable);
    Page<Product> findByNameContainingAndIsHiddenFalse(String name, Pageable pageable);

}
