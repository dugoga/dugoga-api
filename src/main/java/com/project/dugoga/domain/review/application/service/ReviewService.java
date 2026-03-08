package com.project.dugoga.domain.review.application.service;

import com.project.dugoga.domain.order.domain.model.entity.Order;
import com.project.dugoga.domain.order.domain.repository.OrderRepository;
import com.project.dugoga.domain.review.application.dto.ReviewCreateRequestDto;
import com.project.dugoga.domain.review.application.dto.ReviewCreateResponseDto;
import com.project.dugoga.domain.review.application.dto.ReviewGetListResponseDto;
import com.project.dugoga.domain.review.domain.model.entity.Review;
import com.project.dugoga.domain.review.domain.repository.ReviewRepository;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.store.domain.repository.StoreRepository;
import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.domain.user.domain.repository.UserRepository;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public ReviewCreateResponseDto createReview(ReviewCreateRequestDto requestDto, Long userId) {

        UUID storeId = requestDto.getStoreId();
        UUID orderId = requestDto.getOrderId();
        Integer rating = requestDto.getRating();
        String content = requestDto.getContent();
        String imageUrl = requestDto.getImageUrl();

        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Store store = storeRepository.findByIdAndDeletedAtIsNull(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        Review review = Review.builder()
                .storeId(store)
                .userId(user)
                .orderId(order)
                .rating(rating)
                .content(content)
                .imageUrl(imageUrl)
                .isHidden(false)
                .build();

        Review saved = reviewRepository.save(review);

        return ReviewCreateResponseDto.from(saved);
    }

    public ReviewGetListResponseDto getCustomerReview(Pageable pageable, Long userId) {
        Pageable normalized = normalizePageable(pageable);

        Page<Review> page = reviewRepository.findAllByUserId_IdAndDeletedAtIsNull(userId, normalized);

        return ReviewGetListResponseDto.from(page);
    }

    public ReviewGetListResponseDto getStoreReview(Pageable pageable, UUID storeId) {
        Pageable normalized = normalizePageable(pageable);

        Page<Review> page = reviewRepository.findAllByStoreId_IdAndDeletedAtIsNull(storeId, normalized);

        return ReviewGetListResponseDto.from(page);
    }

    private Pageable normalizePageable(Pageable pageable) {

        int page = Math.max(pageable.getPageNumber(), 0);

        int requestedSize = pageable.getPageSize();
        int size = (requestedSize == 10 || requestedSize == 30 || requestedSize == 50)
                ? requestedSize
                : 10;

        Sort sort = pageable.getSort().isSorted()
                ? pageable.getSort()
                : Sort.by(Sort.Direction.DESC, "createdAt");

        return PageRequest.of(page, size, sort);
    }

}
