package com.project.dugoga.domain.order.presentation.controller;

import com.project.dugoga.domain.order.application.dto.*;
import com.project.dugoga.domain.order.application.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    /**
     * TODO: CUSTOMER 권한 처리
     */
    @PostMapping("/orders")
    public ResponseEntity<OrderCreateResponseDto> createOrder(@Valid @RequestBody OrderCreateRequestDto dto) {
        Long userId = 1L; // TODO: Principal 도입 시 삭제 예정
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(userId, dto));
    }

    /**
     * TODO: CUSTOMER 권한 처리
     */
    @GetMapping("/orders")
    public ResponseEntity<UserOrderListResponseDto> searchUserOrderList(
            Pageable pageable,
            String q
    ) {
        Long userId = 1L; // TODO: Principal 도입 시 삭제 예정
        return ResponseEntity.ok(orderService.searchUserOrderList(userId, q, pageable));
    }

    /**
     * TODO: OWNER 권한 처리
     */
    @GetMapping("/stores/{id}/orders")
    public ResponseEntity<OwnerOrderListResponseDto> searchOwnerOrderList(
            @PathVariable("id") UUID storeId,
            Pageable pageable,
            String q
    ) {
        Long userId = 4L; // TODO: Principal 도입 시 삭제 예정
        return ResponseEntity.ok(orderService.searchOwnerOrderList(userId, storeId, q, pageable));
    }

    /**
     * TODO: CUSTOMER 권한 처리
     */
    @GetMapping("/orders/{id}")
    public ResponseEntity<UserOrderDetailResponseDto> getOrderDetail(
            @PathVariable("id") UUID orderId
    ) {
        Long userId = 1L;
        return ResponseEntity.ok(orderService.getOrderDetail(userId, orderId));
    }

    /**
     * TODO: CUSTOMER 권한 처리
     */
    @PostMapping("/orders/{id}/cancel")
    public ResponseEntity<OrderCancelResponseDto> cancelOrder(
            @PathVariable("id") UUID orderId
    ) {
        Long userId = 1L;
        return ResponseEntity.ok(orderService.cancelOrder(userId, orderId));
    }
}
