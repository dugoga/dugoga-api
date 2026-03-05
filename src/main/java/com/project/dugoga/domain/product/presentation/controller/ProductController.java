package com.project.dugoga.domain.product.presentation.controller;

import com.project.dugoga.domain.product.application.dto.ProductCreateRequestDto;
import com.project.dugoga.domain.product.application.dto.ProductCreateResponseDto;
import com.project.dugoga.domain.product.application.dto.ProductDetailsResponseDto;
import com.project.dugoga.domain.product.application.dto.ProductPageResponseDto;
import com.project.dugoga.domain.product.application.service.ProductService;
import com.project.dugoga.domain.user.domain.model.enums.UserRoleEnum;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
        // TODO: 테스트 목적으로 request 에서 사용자 아이디 조회
        ProductCreateResponseDto responseDto = productService.createProduct(request, request.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /*
        TODO: CUSTOMER, OWNER 권한 -> 숨김처리X 상품목록
              MANAGER, MASTER 권한 -> 모든 상품목록
     */
    @GetMapping
    public ResponseEntity<ProductPageResponseDto> getProducts(
            @RequestParam(required = false)
            String search,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
            , @RequestParam String userRole, @RequestParam Long userId  // TODO: 테스트 목적으로 유저 정보 입력받아 사용
    ) {
        Pageable validatedPageable = getValidatedPageable(pageable);
        String trimmedSearch = search != null ? search.trim() : null;
        ProductPageResponseDto responseDto = productService.getProductPage(trimmedSearch, validatedPageable, userId, getUserRole(userRole));
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailsResponseDto> getProductDetails(
            @PathVariable UUID productId
            , @RequestParam String userRole, @RequestParam Long userId  // TODO: 테스트 목적으로 유저 정보 입력받아 사용
    ) {
        ProductDetailsResponseDto responseDto = productService.getProductDetails(productId, userId, getUserRole(userRole));
        return ResponseEntity.ok(responseDto);
    }

    private Pageable getValidatedPageable(Pageable pageable) {
        int size = pageable.getPageSize();
        int validateSize = (size == 10 || size == 30 || size == 50) ? size : 10;
        return PageRequest.of(
                pageable.getPageNumber(),
                validateSize,
                pageable.getSort()
        );
    }

    /*
        TODO: 권한을 RequestParam 으로 받아오도록 함 인증기능 도입시 삭제
     */
    UserRoleEnum getUserRole(String role) {
        if (role.equalsIgnoreCase("CUSTOMER")) {
            return UserRoleEnum.CUSTOMER;
        } else if (role.equalsIgnoreCase("OWNER")) {
            return UserRoleEnum.OWNER;
        } else if (role.equalsIgnoreCase("MANAGER")) {
            return UserRoleEnum.MANAGER;
        }
        return UserRoleEnum.MASTER;
    }
}
