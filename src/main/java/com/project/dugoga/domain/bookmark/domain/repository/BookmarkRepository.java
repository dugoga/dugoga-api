package com.project.dugoga.domain.bookmark.domain.repository;

import com.project.dugoga.domain.bookmark.domain.model.entity.Bookmark;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.user.domain.model.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface BookmarkRepository {
    boolean existsByUser_IdAndStore_Id(Long userId, UUID storeId);

    Bookmark save(Bookmark bookmark);

    Optional<Bookmark> findByStoreAndUserAndDeletedAtIsNull(Store store, User user);

    Page<Bookmark> search(String keyword, Long userId, boolean isAdmin, Pageable normalizePageable);

}
