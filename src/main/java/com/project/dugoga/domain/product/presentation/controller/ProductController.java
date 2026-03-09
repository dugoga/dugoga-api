package com.project.dugoga.domain.product.presentation.controller;

import com.project.dugoga.domain.product.application.dto.*;
import com.project.dugoga.domain.product.application.service.ProductService;
import com.project.dugoga.global.security.jwt.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    /*
        TODO: OWNER, MANAGER, MASTER 권한
     */
    @PreAuthorize("hasAnyRole('MASTER','MANAGER','OWNER')")
    @PostMapping
    public ResponseEntity<ProductCreateResponseDto> createProduct(
            @Valid @RequestBody ProductCreateRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ProductCreateResponseDto responseDto = productService.createProduct(request, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /*
        TODO: CUSTOMER, OWNER 권한 -> 숨김처리X 상품목록
              MANAGER, MASTER 권한 -> 모든 상품목록
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<ProductPageResponseDto> getProducts(
            @RequestParam(required = false)
            String search,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ProductPageResponseDto responseDto = productService.getProductPage(search, pageable, userDetails.getUserRole());
        return ResponseEntity.ok(responseDto);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailsResponseDto> getProductDetails(
            @PathVariable UUID productId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ProductDetailsResponseDto responseDto = productService.getProductDetails(productId, userDetails.getId(), userDetails.getUserRole());
        return ResponseEntity.ok(responseDto);
    }

    /*
        TODO : MASTER, MANAGER, OWNER(본인O) - 숨김 처리된 상품 변경 가능
        OWNER(본인X) - 상품변경 불가
     */
    @PreAuthorize("hasAnyRole('MASTER','MANAGER','OWNER')")
    @PutMapping("/{productId}")
    public ResponseEntity<ProductUpdateResponseDto> updateProduct(
            @PathVariable UUID productId,
            @Valid @RequestBody ProductUpdateRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ProductUpdateResponseDto responseDto = productService.updateProduct(request, productId, userDetails.getId(), userDetails.getUserRole());
        return ResponseEntity.ok(responseDto);
    }

    /*
       TODO : MASTER, MANAGER, OWNER(본인) - 숨김 처리된 상품 변경 가능
    */
    @PreAuthorize("hasAnyRole('MASTER','MANAGER','OWNER')")
    @PutMapping("/visibility")
    public ResponseEntity<ProductVisibilityUpdateResponseDto> updateProductVisibility(
            @Valid @RequestBody ProductVisibilityUpdateRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ProductVisibilityUpdateResponseDto responseDto = productService.updateVisibility(request, userDetails.getId(), userDetails.getUserRole());
        return ResponseEntity.ok(responseDto);
    }

    /*
       TODO : MASTER, MANAGER, OWNER(본인) - 숨김 처리된 상품 변경 가능
    */
    @PreAuthorize("hasAnyRole('MASTER','MANAGER','OWNER')")
    @PutMapping("/status")
    public ResponseEntity<ProductStatusUpdateResponseDto> updateProductStatus(
            @Valid @RequestBody ProductStatusUpdateRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ProductStatusUpdateResponseDto responseDto = productService.updateStatus(request, userDetails.getId(), userDetails.getUserRole());
        return ResponseEntity.ok(responseDto);
    }

    @PreAuthorize("hasAnyRole('MASTER','MANAGER','OWNER')")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable UUID productId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        productService.deleteProduct(productId, userDetails.getId(), userDetails.getUserRole());
        return ResponseEntity.noContent().build();
    }
}
