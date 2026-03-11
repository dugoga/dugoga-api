package com.project.dugoga.domain.store.presentation.controller;

import com.project.dugoga.domain.store.application.dto.*;
import com.project.dugoga.domain.store.application.service.StoreService;
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
@RequestMapping("/api/stores")
@Tag(name = "가게", description = "가게 관련 API")
public class StoreController {
    private final StoreService storeService;

    /*
        OWNER, MANAGER, MASTER 권한
     */
    @Operation(
            summary = "가게 등록",
            description = "가게를 등록합니다. <br>" +
                    "'MASTER', 'MANAGER', 'OWNER' 인 사용자만 접근 가능합니다."
    )
    @PreAuthorize("hasAnyRole('MASTER','MANAGER','OWNER')")
    @PostMapping
    public ResponseEntity<StoreCreateResponseDto> createStore(
            @Valid @RequestBody StoreCreateRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        StoreCreateResponseDto responseDto = storeService.createStore(request, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /*
        OWNER(본인), MASTER, MANAGER - 숨김처리된 가게도 조회
        OWNER(본인X), CUSTOMER - 숨김처리 되지않은 가게 조회
     */
    @Operation(
            summary = "가게 상세 조회",
            description = "가게를 상세 조회합니다. <br>" +
                    "로그인한 사용자만 접근 가능합니다. <br>" +
                    "'MASTER', 'MANAGER', 'OWNER(본인 매장)' 인 사용자는 숨김 처리된 가게를 상세 조회할 수 있습니다. <br>" +
                    "'OWNER(타 매장)', 'CUSTOMER' 인 사용자는 숨김 처리된 가게를 상세 조회할 수 없습니다."
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{storeId}")
    public ResponseEntity<StoreDetailsResponseDto> getStoreDetails(
            @PathVariable UUID storeId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        StoreDetailsResponseDto responseDto = storeService.getStoreDetails(storeId, userDetails.getId(), userDetails.getUserRole());
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(
            summary = "가게 목록 조회",
            description = "가게 목록을 조회합니다. <br>" +
                    "query 파라미터로 '카테고리' 와 '가게명' 으로 검색할 수 있습니다. <br>" +
                    "정렬 조건으로 'reviewCount', 'averageRating' 등을 지정할 수 있습니다. <br>" +
                    "로그인한 사용자만 접근 가능합니다. <br>" +
                    "'MASTER', 'MANAGER' 인 사용자는 숨김 처리된 가게 목록을 조회할 수 있습니다. <br>" +
                    "'OWNER', 'CUSTOMER' 인 사용자는 숨김 처리된 가게 목록을 조회할 수 없습니다. <br>"
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<StorePageResponseDto> getStores(
            StoreSearchCondDto cond,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        StorePageResponseDto responseDto = storeService.getStorePage(cond, userDetails.getId(), userDetails.getUserRole(), pageable);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(
            summary = "가게 상품 목록 조회",
            description = "가게 상품 목록을 조회합니다. <br>" +
                    "query 파라미터로 '상품명' 을 검색할 수 있습니다. <br>" +
                    "정렬 조건으로 'price', 'createdAt' 등을 지정할 수 있습니다. <br>" +
                    "로그인한 사용자만 접근 가능합니다. <br>" +
                    "'MASTER', 'MANAGER', 'OWNER(본인 매장)' 인 사용자는 숨김 처리된 가게 상품 목록을 조회할 수 있습니다. <br>" +
                    "'OWNER(타 매장)', 'CUSTOMER' 인 사용자는 숨김 처리된 가게 상품 목록을 조회할 수 없습니다."
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{storeId}/products")
    public ResponseEntity<StoreProductPageResponseDto> getStoreProducts(
            @PathVariable
            UUID storeId,
            @RequestParam(required = false)
            String search,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        StoreProductPageResponseDto responseDto = storeService.getStoreProductPage(storeId, search, pageable, userDetails.getId(), userDetails.getUserRole());
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(
            summary = "가게 정보 수정",
            description = "가게 정보를 수정합니다. <br>" +
                    "'MASTER', 'MANAGER', 'OWNER(본인 매장)' 인 사용자는 가게 정보를 수정할 수 있습니다."
    )
    @PreAuthorize("hasAnyRole('MASTER','MANAGER','OWNER')")
    @PutMapping("/{storeId}")
    public ResponseEntity<StoreUpdateResponseDto> updateStore(
            @PathVariable UUID storeId,
            @Valid @RequestBody StoreUpdateRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        StoreUpdateResponseDto responseDto = storeService.updateStore(request, storeId, userDetails.getId(), userDetails.getUserRole());
        return ResponseEntity.ok(responseDto);
    }

    @Operation(
            summary = "가게 삭제",
            description = "가게를 삭제합니다. <br>" +
                    "'MASTER', 'MANAGER', 'OWNER(본인 매장)' 인 사용자는 가게를 삭제할 수 있습니다."
    )
    @PreAuthorize("hasAnyRole('MASTER','MANAGER','OWNER')")
    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> deleteStore(
            @PathVariable UUID storeId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        storeService.deleteStore(storeId, userDetails.getId(), userDetails.getUserRole());
        return ResponseEntity.noContent().build();
    }

    // MASTER, MANAGER
    @Operation(
            summary = "가게 숨김 여부 변경",
            description = "가게 숨김 여부를 리스트로 받아 변경합니다. <br>" +
                    "한 번에 100개의 가게의 숨김 여부를 바꿀 수 있습니다. <br> " +
                    "숨김 처리에 성공한 가게와 실패한 가게 아이디 리스트를 반환합니다. <br>" +
                    "'MASTER', 'MANAGER', 인 사용자는 가게 숨김 여부를 변경할 수 있습니다."
    )
    @PreAuthorize("hasAnyRole('MASTER','MANAGER')")
    @PatchMapping("/visibility")
    public ResponseEntity<StoreVisibilityUpdateResponseDto> updateStoreVisibility(
            @Valid @RequestBody StoreVisibilityUpdateRequestDto request
    ) {
        StoreVisibilityUpdateResponseDto responseDto = storeService.visibilityUpdate(request);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(
            summary = "가게 상태 변경",
            description = "가게 상태를 변경합니다. <br>" +
                    "한 번에 100개의 가게의 상태를 바꿀 수 있습니다. <br> " +
                    "상태 변경에 성공한 가게와 실패한 가게 아이디 리스트를 반환합니다. <br>" +
                    "'MASTER', 'MANAGER', 'OWNER(본인 매장)' 인 사용자는 <br>" +
                    "가게 상태를 변경할 수 있습니다. ['OPEN', 'CLOSED', 'PREPARING']"
    )
    @PreAuthorize("hasAnyRole('MASTER','MANAGER','OWNER')")
    @PatchMapping("/status")
    public ResponseEntity<StoreStatusUpdateResponseDto> updateStoreStatus(
            @Valid @RequestBody StoreStatusUpdateRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        StoreStatusUpdateResponseDto responseDto = storeService.statusUpdate(request, userDetails.getId(), userDetails.getUserRole());
        return ResponseEntity.ok(responseDto);
    }
}
