package com.project.dugoga.domain.review.presentation.controller;

import com.project.dugoga.domain.review.application.dto.ReviewCreateRequestDto;
import com.project.dugoga.domain.review.application.dto.ReviewCreateResponseDto;
import com.project.dugoga.domain.review.application.dto.ReviewGetListResponseDto;
import com.project.dugoga.domain.review.application.dto.ReviewGetDetailResponseDto;
import com.project.dugoga.domain.review.application.service.ReviewService;
import com.project.dugoga.global.security.jwt.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
// TODO : 로그인 기능 구현 이후 접근 권한 및 로그인 체크 추가 필요
public class ReviewController {

    private final ReviewService reviewService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<ReviewCreateResponseDto> createReview(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody ReviewCreateRequestDto request)
    {
        ReviewCreateResponseDto responseDto = reviewService.createReview(request, customUserDetails.getId());
        return ResponseEntity.ok(responseDto);
    }

    // Get은 누구나 접근 가능
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ReviewGetListResponseDto> getCustomerReview(
            Pageable pageable, @PathVariable Long customerId)
    {
        ReviewGetListResponseDto responseDto = reviewService.getCustomerReview(pageable, customerId);
        return ResponseEntity.ok(responseDto);
    }

    // Get은 누구나 접근 가능
    @GetMapping("/store/{storeId}")
    public ResponseEntity<ReviewGetListResponseDto> getStoreReview(
            Pageable pageable, @PathVariable UUID storeId)
    {
        ReviewGetListResponseDto responseDto = reviewService.getStoreReview(pageable, storeId);
        return ResponseEntity.ok(responseDto);
    }

    @PreAuthorize("hasAnyRole('MASTER', 'MANAGER')")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID reviewId)
    {
        reviewService.deleteReview(reviewId, customUserDetails.getId());
        return ResponseEntity.noContent().build();
    }

    // Get은 누구나 접근 가능
    @GetMapping("/{id}")
    public ResponseEntity<ReviewGetDetailResponseDto> getReview(
            @PathVariable UUID id)
    {
        ReviewGetDetailResponseDto responseDto = reviewService.getDetailReview(id);
        return ResponseEntity.ok(responseDto);
    }
  
}
