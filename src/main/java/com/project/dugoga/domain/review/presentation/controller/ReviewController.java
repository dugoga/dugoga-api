package com.project.dugoga.domain.review.presentation.controller;

import com.project.dugoga.domain.review.application.dto.ReviewCreateRequestDto;
import com.project.dugoga.domain.review.application.dto.ReviewCreateResponseDto;
import com.project.dugoga.domain.review.application.dto.ReviewGetListResponseDto;
import com.project.dugoga.domain.review.application.dto.ReviewGetDetailResponseDto;
import com.project.dugoga.domain.review.application.service.ReviewService;
import com.project.dugoga.global.security.jwt.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "리뷰", description = "리뷰 API")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(
            summary = "리뷰 등록",
            description = "리뷰를 등록합니다. <br>" +
                    "AI 필터링을 통해 내용에 비속어가 포함된 경우 등록에 실패합니다. <br>" +
                    "'CUSTOMER' 권한을 가진 사용자만 접근 가능합니다."
    )
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
    @Operation(
            summary = "고객별 리뷰 목록 조회",
            description = "고객이 작성한 리뷰 목록을 페이징하여 조회합니다. <br>" +
                    "누구나 접근 가능합니다."
    )
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ReviewGetListResponseDto> getCustomerReview(
            Pageable pageable, @PathVariable Long customerId)
    {
        ReviewGetListResponseDto responseDto = reviewService.getCustomerReview(pageable, customerId);
        return ResponseEntity.ok(responseDto);
    }

    // Get은 누구나 접근 가능
    @Operation(
            summary = "상점별 리뷰 목록 조회",
            description = "상점에 등록된 리뷰 목록을 페이징하여 조회합니다. <br>" +
                    "누구나 접근 가능합니다."
    )
    @GetMapping("/store/{storeId}")
    public ResponseEntity<ReviewGetListResponseDto> getStoreReview(
            Pageable pageable, @PathVariable UUID storeId)
    {
        ReviewGetListResponseDto responseDto = reviewService.getStoreReview(pageable, storeId);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(
            summary = "리뷰 삭제",
            description = "리뷰를 Soft Delete로 삭제합니다. <br>" +
                    "'MASTER', 'MANAGER' 권한을 가진 사용자만 접근 가능합니다."
    )
    @PreAuthorize("hasAnyRole('MASTER', 'MANAGER')")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID reviewId)
    {
        reviewService.deleteReview(reviewId, customUserDetails.getId());
        return ResponseEntity.noContent().build();
    }

    // Get은 누구나 접근 가능
    @Operation(
            summary = "리뷰 상세 조회",
            description = "리뷰의 상세 내용을 조회합니다. <br>" +
                    "누구나 접근 가능합니다."
    )
    @GetMapping("/{id}")
    public ResponseEntity<ReviewGetDetailResponseDto> getReview(
            @PathVariable UUID id)
    {
        ReviewGetDetailResponseDto responseDto = reviewService.getDetailReview(id);
        return ResponseEntity.ok(responseDto);
    }
  
}
