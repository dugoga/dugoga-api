package com.project.dugoga.domain.product.infrastructure.repository;

import com.project.dugoga.domain.product.domain.model.entity.Product;
import com.project.dugoga.domain.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final ProductCustomRepository productCustomRepository;

    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }

    @Override
    public Optional<Product> findByIdAndDeletedAtIsNull(UUID id) {
        return productJpaRepository.findByIdAndDeletedAtIsNull(id);
    }

    @Override
    public List<Product> findAllByStoreIdAndIdInAndDeletedAtIsNull(UUID storeId, Collection<UUID> ids) {
        return productJpaRepository.findAllByStoreIdAndIdInAndDeletedAtIsNull(storeId, ids);
    }

    @Override
    public Optional<Product> findByIdWithStoreAndDeletedAtIsNull(UUID productId) {
        return productJpaRepository.findByIdWithStoreAndDeletedAtIsNull(productId);
    }

    @Override
    public List<Product> findAllByIdInWithStoreAndDeletedAtIsNull(List<UUID> productIds) {
        return productJpaRepository.findAllByIdInWithStoreAndDeletedAtIsNull(productIds);
    }

    public Page<Product> searchStoreProduct(UUID productId, String keyword, boolean isAuthorized, Pageable pageable) {
        return productCustomRepository.searchStoreProduct(productId, keyword, isAuthorized, pageable);
    }

    public Page<Product> searchProduct(String keyword, boolean isAuthorized, Pageable pageable) {
        return productCustomRepository.searchProduct(keyword, isAuthorized, pageable);
    }
}
