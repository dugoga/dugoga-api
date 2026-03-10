package com.project.dugoga.domain.review.infrastructure.repository;

import com.project.dugoga.domain.review.domain.model.entity.Review;
import com.project.dugoga.domain.review.domain.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepository {

    private final ReviewJpaRepository reviewJpaRepository;

    @Override
    public Page<Review> findAllByStoreId(UUID storeId, Pageable pageable) {
        return reviewJpaRepository.findAllByStoreId(storeId, pageable);
    }

    @Override
    public Page<Review> findAllByStoreIdAndDeletedAtIsNull(UUID storeId, Pageable pageable) {
        return reviewJpaRepository.findAllByStoreIdAndDeletedAtIsNull(storeId, pageable);
    }

    @Override
    public Page<Review> findAllByUserId_IdAndDeletedAtIsNull(Long userId, Pageable pageable) {
        return reviewJpaRepository.findAllByUserId_IdAndDeletedAtIsNull(userId, pageable);
    }

    @Override
    public Page<Review> findAllByStoreId_IdAndDeletedAtIsNull(UUID storeId, Pageable pageable) {
        return reviewJpaRepository.findAllByStoreIdAndDeletedAtIsNull(storeId, pageable);
    }

    @Override
    public Optional<Review> findByIdAndDeletedAtIsNull(UUID reviewId) {
        return reviewJpaRepository.findByIdAndDeletedAtIsNull(reviewId);
    }

    @Override
    public boolean existsByOrderId_Id(UUID orderId) {
        return reviewJpaRepository.existsByOrderId_Id(orderId);
    }

    @Override
    public Review save(Review review) {
        return reviewJpaRepository.save(review);
    }

    @Override
    public Optional<Review> findByIdWithStoreAndDeletedAtIsNull(UUID reviewId) {
        return reviewJpaRepository.findByIdWithStoreAndDeletedAtIsNull(reviewId);
    }
}
