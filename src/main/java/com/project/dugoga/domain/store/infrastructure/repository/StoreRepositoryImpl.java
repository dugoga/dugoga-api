package com.project.dugoga.domain.store.infrastructure.repository;

import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.store.domain.repository.StoreRepository;
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
public class StoreRepositoryImpl implements StoreRepository {

    private final StoreJpaRepository storeJpaRepository;
    private final StoreCustomRepository storeCustomRepository;

    @Override
    public Store save(Store store) {
        return storeJpaRepository.save(store);
    }

    @Override
    public Optional<Store> findByIdAndDeletedAtIsNull(UUID storeId) {
        return storeJpaRepository.findByIdAndDeletedAtIsNull(storeId);
    }

    @Override
    public List<Store> findByIdInAndDeletedAtIsNull(Collection<UUID> storeIds) {
        return storeJpaRepository.findByIdInAndDeletedAtIsNull(storeIds);
    }

    @Override
    public Optional<Store> findByIdWithDetailsAndDeletedAtIsNull(UUID storeId) {
        return storeJpaRepository.findByIdWithDetailsAndDeletedAtIsNull(storeId);
    }

    public Page<Store> searchStores(String keyword, String category, Long userId, boolean isAdmin, Pageable pageable) {
        return storeCustomRepository.searchStores(keyword, category, userId, isAdmin, pageable);
    }

}
