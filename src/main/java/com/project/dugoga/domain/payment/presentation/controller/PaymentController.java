package com.project.dugoga.domain.payment.presentation.controller;

import com.project.dugoga.domain.payment.application.dto.PaymentConfirmRequestDto;
import com.project.dugoga.domain.payment.application.dto.PaymentConfirmResponseDto;
import com.project.dugoga.domain.payment.application.service.PaymentService;
import com.project.dugoga.global.security.jwt.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
