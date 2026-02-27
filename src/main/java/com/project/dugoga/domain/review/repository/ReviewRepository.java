package com.project.dugoga.domain.review.repository;

import com.project.dugoga.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    Page<Review> findAllByUserId(Long userId, Pageable pageable);

    Page<Review> findAllByStoreId(UUID storeId, Pageable pageable);

    Page<Review> findAllByStoreIdAndDeletedAtIsNull(UUID storeId, Pageable pageable);
}