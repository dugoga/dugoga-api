package com.project.dugoga.domain.product.domain.repository;

import com.project.dugoga.domain.product.domain.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findAllByStoreIdAndIdIn(UUID storeId, Collection<UUID> ids);
}
