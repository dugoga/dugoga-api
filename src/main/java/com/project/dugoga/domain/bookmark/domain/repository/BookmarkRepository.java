package com.project.dugoga.domain.bookmark.domain.repository;

import com.project.dugoga.domain.bookmark.domain.model.entity.Bookmark;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    boolean existsByUser_IdAndStore_Id(Long userId, UUID storeId);
}
