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

    Page<Product> searchStoreProduct(UUID storeId, String keyword, boolean isAuthorized, Pageable pageable);
    Page<Product> searchProduct(String keyword, boolean isAuthorized, Pageable pageable);

}
