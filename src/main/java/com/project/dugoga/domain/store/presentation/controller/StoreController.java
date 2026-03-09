package com.project.dugoga.domain.store.presentation.controller;

import com.project.dugoga.domain.store.application.dto.*;
import com.project.dugoga.domain.store.application.service.StoreService;
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
@RequestMapping("/api/stores")
public class StoreController {
    private final StoreService storeService;

    /*
        OWNER, MANAGER, MASTER 권한
     */
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
    @GetMapping("/{storeId}")
    public ResponseEntity<StoreDetailsResponseDto> getStoreDetails(
            @PathVariable UUID storeId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        StoreDetailsResponseDto responseDto = storeService.getStoreDetails(storeId, userDetails.getId(), userDetails.getUserRole());
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

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

    @PreAuthorize("hasAnyRole('MASTER','MANAGER','OWNER')")
    @PatchMapping("/{storeId}")
    public ResponseEntity<StoreUpdateResponseDto> updateStore(
            @PathVariable UUID storeId,
            @Valid @RequestBody StoreUpdateRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        StoreUpdateResponseDto responseDto = storeService.updateStore(request, storeId, userDetails.getId(), userDetails.getUserRole());
        return ResponseEntity.ok(responseDto);
    }

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
    @PreAuthorize("hasAnyRole('MASTER','MANAGER')")
    @PatchMapping("/visibility")
    public ResponseEntity<StoreVisibilityUpdateResponseDto> updateStoreVisibility(
            @Valid @RequestBody StoreVisibilityUpdateRequestDto request
    ) {
        StoreVisibilityUpdateResponseDto responseDto = storeService.visibilityUpdate(request);
        return ResponseEntity.ok(responseDto);
    }

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
