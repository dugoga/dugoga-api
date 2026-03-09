package com.project.dugoga.domain.payment.presentation.controller;

import com.project.dugoga.domain.payment.application.dto.PaymentConfirmRequestDto;
import com.project.dugoga.domain.payment.application.dto.PaymentConfirmResponseDto;
import com.project.dugoga.domain.payment.application.dto.UserPaymentDetailResponseDto;
import com.project.dugoga.domain.payment.application.dto.UserPaymentListResponseDto;
import com.project.dugoga.domain.payment.application.service.PaymentService;
import com.project.dugoga.global.security.jwt.CustomUserDetails;
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
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/payments/confirm")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PaymentConfirmResponseDto> confirmPayment(
            @Valid @RequestBody PaymentConfirmRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(paymentService.confirmPayment(userDetails.getId(), dto));
    }

    @GetMapping("/payments")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<UserPaymentListResponseDto> searchPayments(
            @RequestParam(required = false) String q,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(paymentService.searchPayments(userDetails.getId(), q, pageable));
    }

    @GetMapping("/payments/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<UserPaymentDetailResponseDto> getPayment(
            @PathVariable("id") UUID paymentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(paymentService.findPayment(userDetails.getId(), paymentId));
    }
}
