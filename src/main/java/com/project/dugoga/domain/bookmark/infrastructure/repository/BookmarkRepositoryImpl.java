package com.project.dugoga.domain.bookmark.infrastructure.repository;

import com.project.dugoga.domain.bookmark.domain.model.entity.Bookmark;
import com.project.dugoga.domain.bookmark.domain.repository.BookmarkRepository;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.user.domain.model.entity.User;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookmarkRepositoryImpl implements BookmarkRepository {

    private final BookmarkJpaRepository jpaRepository;
    private final BookmarkCustomRepository customRepository;

    @Override
    public boolean existsByUser_IdAndStore_Id(Long userId, UUID storeId) {
        return jpaRepository.existsByUser_IdAndStore_Id(userId, storeId);
    }

    @Override
    public Optional<Bookmark> findByStoreAndUserAndDeletedAtIsNull(Store store, User user) {
        return jpaRepository.findByStoreAndUserAndDeletedAtIsNull(store, user);
    }

    @Override
    public Page<Bookmark> search(String keyword, Long userId, boolean isAdmin, Pageable normalizePageable) {
        return customRepository.search(keyword, userId, isAdmin, normalizePageable);
    }

    @Override
    public Bookmark save(Bookmark bookmark) {
        return jpaRepository.save(bookmark);
    }
}
