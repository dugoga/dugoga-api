package com.project.dugoga.domain.bookmark.domain.repository;

import com.project.dugoga.domain.bookmark.domain.model.entity.Bookmark;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.user.domain.model.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    boolean existsByUser_IdAndStore_Id(Long userId, UUID storeId);

    Optional<Bookmark> findByStoreAndUser(Store store, User user);

    Page<Bookmark> findByUserAndDeletedAtIsNull(User user, Pageable pageable);

    Page<Bookmark> findByUserAndStore_NameContainingAndDeletedAtIsNull(User user, String keyword, Pageable normalized);
}
