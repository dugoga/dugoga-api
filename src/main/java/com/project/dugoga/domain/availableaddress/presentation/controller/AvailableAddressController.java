package com.project.dugoga.domain.availableaddress.presentation.controller;

import com.project.dugoga.domain.availableaddress.application.dto.AvailableAddressCreateRequestDto;
import com.project.dugoga.domain.availableaddress.application.dto.AvailableAddressCreateResponseDto;
import com.project.dugoga.domain.availableaddress.application.dto.AvailableAddressUpdateRequestDto;
import com.project.dugoga.domain.availableaddress.application.dto.AvailableAddressUpdateResponseDto;
import com.project.dugoga.domain.availableaddress.application.dto.AvailableAddressListDto;
import com.project.dugoga.domain.availableaddress.application.service.AvailableAddressService;
import com.project.dugoga.global.security.jwt.CustomUserDetails;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/api/service-areas")
public class AvailableAddressController {

    private final AvailableAddressService availableAddressService;


    @PreAuthorize("hasAnyRole('MASTER', 'MANAGER')")
    @PostMapping
    public ResponseEntity<AvailableAddressCreateResponseDto> createAvailableAddress(@Valid @RequestBody AvailableAddressCreateRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(availableAddressService.createAvailableAddress(dto));
    }

    @PreAuthorize("hasAnyRole('MASTER', 'MANAGER')")
    @PatchMapping("/{areaId}")
    public ResponseEntity<AvailableAddressUpdateResponseDto> updateAvailableAddress(
            @PathVariable UUID areaId,
            @Valid @RequestBody AvailableAddressUpdateRequestDto request) {
        return ResponseEntity.ok(availableAddressService.updateAvailableAddress(areaId, request));
    }

    @PreAuthorize("hasAnyRole('MASTER', 'MANAGER')")
    @DeleteMapping("/{areaId}")
    public ResponseEntity<Void> deleteAvailableAddress(@PathVariable UUID areaId,
                                                       @AuthenticationPrincipal CustomUserDetails details) {
        availableAddressService.deleteAvailableAddress(areaId, details.getId());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<AvailableAddressListDto> searchAvailableAddress(
            Pageable pageable,
            @RequestParam(required = false) String query
    ) {
        return ResponseEntity.ok(availableAddressService.searchAvailableAddress(pageable, query));
    }


}