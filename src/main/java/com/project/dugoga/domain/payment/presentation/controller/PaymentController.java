package com.project.dugoga.domain.payment.presentation.controller;

import com.project.dugoga.domain.payment.application.dto.PaymentConfirmRequestDto;
import com.project.dugoga.domain.payment.application.dto.PaymentConfirmResponseDto;
import com.project.dugoga.domain.payment.application.dto.UserPaymentDetailResponseDto;
import com.project.dugoga.domain.payment.application.dto.UserPaymentListResponseDto;
import com.project.dugoga.domain.payment.application.service.PaymentService;
import com.project.dugoga.global.security.jwt.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "결제", description = "payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/payments/confirm")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(
            summary = "결제 승인",
            description = "CUSTOMER 권한을 가진 사용자만 결제 승인이 가능합니다."
    )
    public ResponseEntity<PaymentConfirmResponseDto> confirmPayment(
            @Valid @RequestBody PaymentConfirmRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(paymentService.confirmPayment(userDetails.getId(), dto));
    }

    @GetMapping("/payments")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(
            summary = "결제 목록 조회",
            description = "CUSTOMER 권한을 가진 사용자만 결제 목록 조회가 가능합니다."
    )
    public ResponseEntity<UserPaymentListResponseDto> searchPayments(
            @RequestParam(required = false) String q,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(paymentService.searchPayments(userDetails.getId(), q, pageable));
    }

    @GetMapping("/payments/{paymentId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(
            summary = "결제 상세 조회",
            description = "CUSTOMER 권한을 가진 사용자만 결제 상세 조회가 가능합니다."
    )
    public ResponseEntity<UserPaymentDetailResponseDto> getPayment(
            @PathVariable UUID paymentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(paymentService.findPayment(userDetails.getId(), paymentId));
    }
}
