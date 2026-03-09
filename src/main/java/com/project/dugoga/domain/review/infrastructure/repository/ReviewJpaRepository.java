package com.project.dugoga.domain.review.infrastructure.repository;

import com.project.dugoga.domain.review.domain.model.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ReviewJpaRepository extends JpaRepository<Review, UUID> {
    Page<Review> findAllByStoreId(UUID storeId, Pageable pageable);

    Page<Review> findAllByStoreIdAndDeletedAtIsNull(UUID storeId, Pageable pageable);

    Page<Review> findAllByUserId_IdAndDeletedAtIsNull(Long userId, Pageable pageable);

    Page<Review> findAllByStoreId_IdAndDeletedAtIsNull(UUID storeId, Pageable pageable);

    Optional<Review> findByIdAndDeletedAtIsNull(UUID reviewId);

    @Query("select r from Review r join fetch r.storeId where r.id = :reviewId and r.deletedAt is null ")
    Optional<Review> findByIdWithStoreAndDeletedAtIsNull(@Param("reviewId") UUID reviewId);

    boolean existsByOrderId_Id(UUID orderId);
}
