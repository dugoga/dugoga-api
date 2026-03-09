package com.project.dugoga.domain.order.presentation.controller;

import com.project.dugoga.domain.order.application.dto.*;
import com.project.dugoga.domain.order.application.service.OrderService;
import com.project.dugoga.global.security.jwt.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderCreateResponseDto> createOrder(
            @Valid @RequestBody OrderCreateRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(userDetails.getId(), dto));
    }

    @GetMapping("/orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<UserOrderListResponseDto> searchUserOrderList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable,
            String q
    ) {
        return ResponseEntity.ok(orderService.searchUserOrderList(userDetails.getId(), q, pageable));
    }

    @GetMapping("/stores/{id}/orders")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<OwnerOrderListResponseDto> searchOwnerOrderList(
            @PathVariable("id") UUID storeId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable,
            String q
    ) {
        return ResponseEntity.ok(orderService.searchOwnerOrderList(userDetails.getId(), storeId, q, pageable));
    }

    @GetMapping("/orders/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<UserOrderDetailResponseDto> getOrderDetail(
            @PathVariable("id") UUID orderId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(orderService.getOrderDetail(userDetails.getId(), orderId));
    }

    @PostMapping("/orders/{id}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderCancelResponseDto> cancelOrder(
            @PathVariable("id") UUID orderId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(orderService.cancelOrder(userDetails.getId(), orderId));
    }

    @PostMapping("/orders/{id}/accept")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<OrderAcceptResponseDto> acceptOrder(
            @PathVariable("id") UUID orderId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(orderService.acceptOrder(userDetails.getId(), orderId));
    }

    @PostMapping("/orders/{id}/reject")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<OrderRejectResponseDto> rejectOrder(
            @PathVariable("id") UUID orderId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(orderService.rejectOrder(userDetails.getId(), orderId));
    }
}
