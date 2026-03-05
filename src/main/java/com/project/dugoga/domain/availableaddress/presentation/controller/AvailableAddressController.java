package com.project.dugoga.domain.availableaddress.presentation.controller;

import com.project.dugoga.domain.availableaddress.application.dto.AvailableAddressCreateRequestDto;
import com.project.dugoga.domain.availableaddress.application.dto.AvailableAddressCreateResponseDto;
import com.project.dugoga.domain.availableaddress.application.dto.AvailableAddressUpdateRequestDto;
import com.project.dugoga.domain.availableaddress.application.dto.AvailableAddressUpdateResponseDto;
import com.project.dugoga.domain.availableaddress.application.dto.AvailableAddressUserListDto;
import com.project.dugoga.domain.availableaddress.application.service.AvailableAddressService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AvailableAddressController {

    private final AvailableAddressService availableAddressService;
    /*
     *  todo: 권한 처리 - MANAGER, MASTER
     * */
    @PostMapping
    public ResponseEntity<AvailableAddressCreateResponseDto> createAvailableAddress(@Valid @RequestBody AvailableAddressCreateRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(availableAddressService.createAvailableAddress(dto));
    }

    @PatchMapping("/service-areas/{areaId}")
    public ResponseEntity<AvailableAddressUpdateResponseDto> updateAvailableAddress(
            @PathVariable UUID areaId,
            @Valid @RequestBody AvailableAddressUpdateRequestDto request) {
        return ResponseEntity.ok(availableAddressService.updateAvailableAddress(areaId, request));
    }

    @DeleteMapping("/service-areas/{areaId}")
    public ResponseEntity<Void> deleteAvailableAddress(@PathVariable UUID areaId) {
        // todo: userId 가져오기
        Long userId = 1L;
        availableAddressService.deleteAvailableAddress(areaId, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/service-areas/{areaId}/restore")
    public ResponseEntity<AvailableAddressUpdateResponseDto> restoreAvailableAddress(@PathVariable UUID areaId) {
        return ResponseEntity.ok(availableAddressService.restore(areaId));
    }

    @GetMapping("/service-areas")
    public ResponseEntity<AvailableAddressUserListDto> searchUserAvailableAddress(
            Pageable pageable,
            @RequestParam(required = false) String query
    ) {
        return ResponseEntity.ok(availableAddressService.searchUserAvailableAddress(pageable, query));
    }

    @GetMapping("/admin/service-areas")
    public ResponseEntity<AvailableAddressUserListDto> searchAdminAvailableAddress(
            Pageable pageable,
            @RequestParam(required = false) String query
    ) {
        return ResponseEntity.ok(availableAddressService.searchAdminAvailableAddress(pageable, query));
    }

}