package com.project.dugoga.domain.review.presentation.controller;

import com.project.dugoga.domain.review.application.dto.ReviewCreateRequestDto;
import com.project.dugoga.domain.review.application.dto.ReviewCreateResponseDto;
import com.project.dugoga.domain.review.application.dto.ReviewGetListResponseDto;
import com.project.dugoga.domain.review.application.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
// TODO : 로그인 기능 구현 이후 접근 권한 및 로그인 체크 추가 필요
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewCreateResponseDto> createReview(
            @Valid @RequestBody ReviewCreateRequestDto request)
    {
        Long userId = 1L;
        ReviewCreateResponseDto responseDto = reviewService.createReview(request, userId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ReviewGetListResponseDto> getCustomerReview(
            Pageable pageable, @PathVariable Long customerId)
    {
        ReviewGetListResponseDto responseDto = reviewService.getCustomerReview(pageable, customerId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<ReviewGetListResponseDto> getStoreReview(
            Pageable pageable, @PathVariable UUID storeId)
    {
        ReviewGetListResponseDto responseDto = reviewService.getStoreReview(pageable, storeId);
        return ResponseEntity.ok(responseDto);
    }

}
