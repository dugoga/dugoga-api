package com.project.dugoga.domain.bookmark.infrastructure.repository;

import com.project.dugoga.domain.bookmark.domain.model.entity.Bookmark;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.user.domain.model.entity.User;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkJpaRepository extends JpaRepository<Bookmark, UUID> {
    boolean existsByUser_IdAndStore_Id(Long userId, UUID storeId);

    Optional<Bookmark> findByStoreAndUserAndDeletedAtIsNull(Store store, User user);
}
