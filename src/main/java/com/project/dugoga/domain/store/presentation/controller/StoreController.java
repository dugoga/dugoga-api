package com.project.dugoga.domain.store.presentation.controller;

import com.project.dugoga.domain.store.application.dto.*;
import com.project.dugoga.domain.store.application.service.StoreService;
import com.project.dugoga.domain.user.domain.model.enums.UserRoleEnum;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @PatchMapping("/visibility")
    public ResponseEntity<StoreVisibilityUpdateResponseDto> updateStoreVisibility(
            @Valid @RequestBody StoreVisibilityUpdateRequestDto request
    ) {
        // TODO: 테스트 목적으로 request 에서 사용자 아이디 조회
        StoreVisibilityUpdateResponseDto responseDto = storeService.visibilityUpdate(request);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/status")
    public ResponseEntity<StoreStatusUpdateResponse> updateStoreStatus(
            @Valid @RequestBody StoreStatusUpdateRequest request
    ) {
        // TODO: 테스트 목적으로 request 에서 사용자 아이디 조회
        StoreStatusUpdateResponse responseDto = storeService.statusUpdate(request, request.getUserId());
        return ResponseEntity.ok(responseDto);
    }
}
