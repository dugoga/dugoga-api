package com.project.dugoga.domain.order.presentation.controller;

import com.project.dugoga.domain.order.application.dto.OrderCreateRequestDto;
import com.project.dugoga.domain.order.application.dto.OrderCreateResponseDto;
import com.project.dugoga.domain.order.application.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/orders")
    public ResponseEntity<OrderCreateResponseDto> createOrder(@Valid @RequestBody OrderCreateRequestDto dto) {
        Long userId = 1L; // TODO: Principal 도입 시 삭제 예정
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(userId, dto));
    }
}
