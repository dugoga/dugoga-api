package com.project.dugoga.domain.product.presentation.controller;

import com.project.dugoga.domain.product.application.dto.*;
import com.project.dugoga.domain.product.application.service.ProductService;
import com.project.dugoga.global.security.jwt.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "상품", description = "상품 관련 API")
public class ProductController {
    private final ProductService productService;

    /*
        TODO: OWNER, MANAGER, MASTER 권한
     */
    @Operation(
            summary = "상품 등록",
            description = "상품을 등록합니다. <br>" +
                    "'MASTER', 'MANAGER', 'OWNER(본인 매장)' 인 사용자만 접근 가능합니다."
    )
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
    @Operation(
            summary = "상품 목록 조회",
            description = "상품 목록을 조회합니다. <br>" +
                    "query 파라미터로 '상품명' 을 검색할 수 있습니다. <br>" +
                    "정렬 조건으로 'price', 'createdAt' 등을 지정할 수 있습니다. <br>" +
                    "로그인한 사용자만 접근 가능합니다. <br>" +
                    "'MASTER', 'MANAGER' 인 사용자는 숨김 처리된 상품 목록을 조회할 수 있습니다. <br>" +
                    "'OWNER', 'CUSTOMER' 인 사용자는 숨김 처리된 상품 목록을 조회할 수 없습니다. <br>"
    )
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

    @Operation(
            summary = "상품 상세 조회",
            description = "상품을 상세 조회합니다. <br>" +
                    "로그인한 사용자만 접근 가능합니다. <br>" +
                    "'MASTER', 'MANAGER', 'OWNER(본인 매장)' 인 사용자는 숨김 처리된 상품을 상세 조회할 수 있습니다. <br>" +
                    "'OWNER(타 매장)', 'CUSTOMER' 인 사용자는 숨김 처리된 상품을 상세 조회할 수 없습니다."
    )
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
    @Operation(
            summary = "상품 정보 수정",
            description = "상품 정보를 수정합니다. <br>" +
                    "'MASTER', 'MANAGER', 'OWNER(본인 매장)' 인 사용자는 상품 정보를 수정할 수 있습니다."
    )
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
    @Operation(
            summary = "상품 숨김 여부 변경",
            description = "상품 숨김 여부를 변경합니다. <br>" +
                    "한 번에 100개의 상품 숨김 여부를 바꿀 수 있습니다. <br> " +
                    "숨김 처리 성공한 상품과 실패한 상품 아이디 리스트를 반환합니다. <br>" +
                    "'MASTER', 'MANAGER', 'OWNER(본인 매장)' 인 사용자는 상품 숨김 여부를 변경할 수 있습니다."
    )
    @PreAuthorize("hasAnyRole('MASTER','MANAGER','OWNER')")
    @PatchMapping("/visibility")
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
    @Operation(
            summary = "상품 품절 여부 변경",
            description = "상품 품절 여부를 변경합니다. <br>" +
                    "한 번에 100개의 상품 품절 여부를 바꿀 수 있습니다. <br> " +
                    "품절 처리 성공한 상품과 실패한 상품 아이디 리스트를 반환합니다. <br>" +
                    "'MASTER', 'MANAGER', 'OWNER(본인 매장)' 인 사용자는 상품 품절 여부를 변경할 수 있습니다."
    )
    @PreAuthorize("hasAnyRole('MASTER','MANAGER','OWNER')")
    @PatchMapping("/status")
    public ResponseEntity<ProductStatusUpdateResponseDto> updateProductStatus(
            @Valid @RequestBody ProductStatusUpdateRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ProductStatusUpdateResponseDto responseDto = productService.updateStatus(request, userDetails.getId(), userDetails.getUserRole());
        return ResponseEntity.ok(responseDto);
    }

    @Operation(
            summary = "상품 삭제",
            description = "상품을 삭제합니다. <br>" +
                    "'MASTER', 'MANAGER', 'OWNER(본인 매장)' 인 사용자는 상품을 삭제할 수 있습니다."
    )
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
