package com.project.dugoga.domain.review.domain.repository;

import com.project.dugoga.domain.review.domain.model.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository {

    Page<Review> findAllByStoreId(UUID storeId, Pageable pageable);

    Page<Review> findAllByStoreIdAndDeletedAtIsNull(UUID storeId, Pageable pageable);

    Page<Review> findAllByUserId_IdAndDeletedAtIsNull(Long userId, Pageable pageable);

    Page<Review> findAllByStoreId_IdAndDeletedAtIsNull(UUID storeId, Pageable pageable);

    Optional<Review> findByIdAndDeletedAtIsNull(UUID reviewId);

    Optional<Review> findByIdWithStoreAndDeletedAtIsNull(UUID reviewId);

    boolean existsByOrderId_Id(UUID orderId);

    Review save(Review review);

}
