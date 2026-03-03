package com.project.dugoga.domain.store.presentation.controller;

import com.project.dugoga.domain.store.application.dto.*;
import com.project.dugoga.domain.store.application.service.StoreService;
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
@RequestMapping("/api/stores")
public class StoreController {
    private final StoreService storeService;

    @PostMapping
    public ResponseEntity<StoreCreateResponseDto> createStore(
            @Valid @RequestBody StoreCreateRequestDto request
    ) {
        // TODO: 테스트 목적으로 request 에서 사용자 아이디 조회
        StoreCreateResponseDto responseDto = storeService.createStore(request, request.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /*
        OWNER(본인), MASTER, MANAGER - 숨김처리된 가게도 조회
        OWNER(본인X), CUSTOMER - 숨김처리 되지않은 가게 조회
     */
    @GetMapping("/{storeId}")
    public ResponseEntity<StoreDetailsResponseDto> getStoerDetails(
            @PathVariable UUID storeId
    ) {
        // TODO: 테스트 목적으로 사용자 아이디, 권한 직접 지정
        Long userId = 4L;
        UserRoleEnum userRole = UserRoleEnum.CUSTOMER;
        StoreDetailsResponseDto responseDto = storeService.getStoreDetails(storeId, userId, userRole);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<StorePageResponseDto> getStores(
            @RequestParam(required = false)
            String search,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        // TODO: 테스트 목적으로 사용자 권한 직접 지정
        UserRoleEnum userRole = UserRoleEnum.MANAGER;
        Pageable validatedPageable = getValidatedPageable(pageable);
        String trimmedSearch = search != null ? search.trim() : null;
        StorePageResponseDto responseDto = storeService.getStorePage(trimmedSearch, validatedPageable, userRole);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/{storeId}/products")
    public ResponseEntity<StoreProductPageResponseDto> getStoreProducts(
            @PathVariable
            UUID storeId,
            @RequestParam(required = false)
            String search,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        UserRoleEnum userRole = UserRoleEnum.CUSTOMER;
        Pageable validatedPageable = getValidatedPageable(pageable);
        String trimmedSearch = search != null ? search.trim() : null;
        StoreProductPageResponseDto responseDto = storeService.getStoreProductPage(storeId, trimmedSearch, validatedPageable, userRole);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PatchMapping("/{storeId}")
    public ResponseEntity<StoreUpdateResponseDto> updateStore(
            @PathVariable UUID storeId,
            @Valid @RequestBody StoreUpdateRequestDto request
    ) {
        // TODO: 테스트 목적으로 request 에서 사용자 아이디 조회
        StoreUpdateResponseDto responseDto = storeService.updateStore(request, storeId, request.getUserId(), request.getUserRole());
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{storeId}")
    public ResponseEntity<StoreUpdateResponseDto> deleteStore(
            @PathVariable UUID storeId
    ) {
        Long userId = 4L; // TODO: 테스트 목적으로 사용자 아이디 직접 지정
        storeService.deleteStore(storeId, userId, UserRoleEnum.OWNER); // TODO: 테스트 목적으로 UserRole 직접 지정
        return ResponseEntity.noContent().build();
    }

    // MASTER, MANAGER
    @PatchMapping("/visibility")
    public ResponseEntity<StoreVisibilityUpdateResponseDto> updateStoreVisibility(
            @Valid @RequestBody StoreVisibilityUpdateRequestDto request
    ) {
        // TODO: 테스트 목적으로 request 에서 사용자 아이디 조회
        StoreVisibilityUpdateResponseDto responseDto = storeService.visibilityUpdate(request);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/status")
    public ResponseEntity<StoreStatusUpdateResponseDto> updateStoreStatus(
            @Valid @RequestBody StoreStatusUpdateRequestDto request
    ) {
        // TODO: 테스트 목적으로 request 에서 사용자 아이디 조회
        StoreStatusUpdateResponseDto responseDto = storeService.statusUpdate(request, request.getUserId(), request.getUserRole());
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
}
