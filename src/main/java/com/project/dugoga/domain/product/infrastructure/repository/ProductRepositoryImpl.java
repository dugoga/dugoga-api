package com.project.dugoga.domain.product.infrastructure.repository;

import com.project.dugoga.domain.product.domain.model.entity.Product;
import com.project.dugoga.domain.product.domain.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ProductRepositoryImpl extends JpaRepository<Product, UUID>, ProductRepository {
    List<Product> findAllByStoreIdAndIdIn(UUID storeId, Collection<UUID> ids);

    Page<Product> findByStoreIdAndIsHiddenFalse(UUID storeId, Pageable pageable);

    Page<Product> findByStoreId(UUID storeId, Pageable pageable);

    Page<Product> findByStoreIdAndNameContaining(UUID storeId, String name, Pageable pageable);

    Page<Product> findByStoreIdAndNameContainingAndIsHiddenFalse(UUID storeId, String name, Pageable pageable);

    Page<Product> findByNameContaining(String name, Pageable pageable);

    Page<Product> findByIsHiddenFalse(Pageable pageable);

    Page<Product> findByNameContainingAndIsHiddenFalse(String name, Pageable pageable);
}
