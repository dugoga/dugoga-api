package com.project.dugoga.domain.product.presentation.controller;

import com.project.dugoga.domain.product.application.dto.ProductCreateRequestDto;
import com.project.dugoga.domain.product.application.dto.ProductCreateResponseDto;
import com.project.dugoga.domain.product.application.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    /*
        TODO: OWNER, MANAGER, MASTER 권한
     */
    @PostMapping
    public ResponseEntity<ProductCreateResponseDto> createProduct(
            @Valid @RequestBody ProductCreateRequestDto request
    ) {
        ProductCreateResponseDto responseDto = productService.createProduct(request, request.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
