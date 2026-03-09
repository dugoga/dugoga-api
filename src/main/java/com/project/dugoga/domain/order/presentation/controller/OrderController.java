package com.project.dugoga.domain.order.presentation.controller;

import com.project.dugoga.domain.order.application.dto.*;
import com.project.dugoga.domain.order.application.service.OrderService;
import com.project.dugoga.global.security.jwt.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "주문", description = "orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(
            summary = "주문 생성",
            description = "CUSTOMER 권한을 가진 사용자만 주문 생성이 가능합니다."
    )
    public ResponseEntity<OrderCreateResponseDto> createOrder(
            @Valid @RequestBody OrderCreateRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(userDetails.getId(), dto));
    }

    @GetMapping("/orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(
            summary = "주문 목록 조회 (CUSTOMER)",
            description = "CUSTOMER 권한을 가진 사용자만 주문 목록 조회가 가능합니다."
    )
    public ResponseEntity<UserOrderListResponseDto> searchUserOrderList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable,
            String q
    ) {
        return ResponseEntity.ok(orderService.searchUserOrderList(userDetails.getId(), q, pageable));
    }

    @GetMapping("/stores/{storeId}/orders")
    @PreAuthorize("hasRole('OWNER')")
    @Operation(
            summary = "주문 목록 조회 (OWNER)",
            description = "OWNER 권한을 가진 사용자만 주문 목록 조회가 가능합니다."
    )
    public ResponseEntity<OwnerOrderListResponseDto> searchOwnerOrderList(
            @PathVariable UUID storeId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable,
            String q
    ) {
        return ResponseEntity.ok(orderService.searchOwnerOrderList(userDetails.getId(), storeId, q, pageable));
    }

    @GetMapping("/orders/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(
            summary = "주문 상세 조회",
            description = "CUSTOMER 권한을 가진 사용자만 주문 생성이 가능합니다."
    )
    public ResponseEntity<UserOrderDetailResponseDto> getOrderDetail(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(orderService.getOrderDetail(userDetails.getId(), orderId));
    }

    @PostMapping("/orders/{orderId}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(
            summary = "주문 취소",
            description = "CUSTOMER 권한을 가진 사용자만 주문 취소가 가능합니다."
    )
    public ResponseEntity<OrderCancelResponseDto> cancelOrder(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(orderService.cancelOrder(userDetails.getId(), orderId));
    }

    @PostMapping("/orders/{orderId}/accept")
    @PreAuthorize("hasRole('OWNER')")
    @Operation(
            summary = "주문 수락",
            description = "OWNER 권한을 가진 사용자만 주문 수락이 가능합니다."
    )
    public ResponseEntity<OrderAcceptResponseDto> acceptOrder(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(orderService.acceptOrder(userDetails.getId(), orderId));
    }

    @PostMapping("/orders/{orderId}/reject")
    @PreAuthorize("hasRole('OWNER')")
    @Operation(
            summary = "주문 거절",
            description = "OWNER 권한을 가진 사용자만 주문 거절이 가능합니다."
    )
    public ResponseEntity<OrderRejectResponseDto> rejectOrder(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(orderService.rejectOrder(userDetails.getId(), orderId));
    }
}
