package com.project.dugoga.domain.availableaddress.presentation.controller;

import com.project.dugoga.domain.availableaddress.application.dto.AvailableAddressCreateRequestDto;
import com.project.dugoga.domain.availableaddress.application.dto.AvailableAddressCreateResponseDto;
import com.project.dugoga.domain.availableaddress.application.dto.AvailableAddressUpdateRequestDto;
import com.project.dugoga.domain.availableaddress.application.dto.AvailableAddressUpdateResponseDto;
import com.project.dugoga.domain.availableaddress.application.dto.AvailableAddressListDto;
import com.project.dugoga.domain.availableaddress.application.service.AvailableAddressService;
import com.project.dugoga.global.security.jwt.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "서비스 지역", description = "서비스 지역 관련 API")
public class AvailableAddressController {

    private final AvailableAddressService availableAddressService;

    @Operation(
            summary = "서비스 지역 등록",
            description = "서비스 지역을 등록합니다. 역할이 MASTER, MANAGER인 사용자만 접근 가능합니다."
    )
    @PreAuthorize("hasAnyRole('MASTER', 'MANAGER')")
    @PostMapping
    public ResponseEntity<AvailableAddressCreateResponseDto> createAvailableAddress(@Valid @RequestBody AvailableAddressCreateRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(availableAddressService.createAvailableAddress(dto));
    }

    @Operation(
            summary = "서비스 지역 졍보 수정",
            description = "서비스 지역 정보를 수정합니다. 역할이 MASTER, MANAGER인 사용자만 접근 가능합니다."
    )
    @PreAuthorize("hasAnyRole('MASTER', 'MANAGER')")
    @PatchMapping("/{areaId}")
    public ResponseEntity<AvailableAddressUpdateResponseDto> updateAvailableAddress(
            @PathVariable UUID areaId,
            @Valid @RequestBody AvailableAddressUpdateRequestDto request) {
        return ResponseEntity.ok(availableAddressService.updateAvailableAddress(areaId, request));
    }

    @Operation(
            summary = "서비스 지역 삭제",
            description = "서비스 지역을 삭제합니다. "
                    + "실제 데이터는 삭제되지 않고 논리삭제를 합니다. "
                    + "역할이 MASTER, MANAGER인 사용자만 접근 가능합니다."
    )
    @PreAuthorize("hasAnyRole('MASTER', 'MANAGER')")
    @DeleteMapping("/{areaId}")
    public ResponseEntity<Void> deleteAvailableAddress(@PathVariable UUID areaId,
                                                       @AuthenticationPrincipal CustomUserDetails details) {
        availableAddressService.deleteAvailableAddress(areaId, details.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "서비스 지역 조회",
            description = "서비스 가능한 지역을 조회합니다. "
                    + "query 파라미터로 지역명을 검색할 수 있습니다. "
                    + "로그인 한 사용자만 접근 가능합니다."
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<AvailableAddressListDto> searchAvailableAddress(
            Pageable pageable,
            @RequestParam(required = false) String query
    ) {
        return ResponseEntity.ok(availableAddressService.searchAvailableAddress(pageable, query));
    }


}