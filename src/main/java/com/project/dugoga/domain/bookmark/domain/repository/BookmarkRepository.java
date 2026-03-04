package com.project.dugoga.domain.bookmark.domain.repository;

import com.project.dugoga.domain.bookmark.domain.model.entity.Bookmark;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.user.domain.model.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, UUID> {
    boolean existsByUser_IdAndStore_Id(Long userId, UUID storeId);

    Optional<Bookmark> findByStoreAndUser(Store store, User user);

    @EntityGraph(attributePaths = "store")
    Page<Bookmark> findByUserAndDeletedAtIsNull(User user, Pageable pageable);

    @EntityGraph(attributePaths = "store")
    Page<Bookmark> findByUserAndStore_NameContainingAndDeletedAtIsNull(User user, String keyword, Pageable normalized);

    @EntityGraph(attributePaths = "store")
    Page<Bookmark> findByUser(User user, Pageable pageable);

    @EntityGraph(attributePaths = "store")
    Page<Bookmark> findByUserAndStore_NameContaining(User user, String keyword, Pageable pageable);
}
