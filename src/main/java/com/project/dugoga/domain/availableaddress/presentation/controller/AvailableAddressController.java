package com.project.dugoga.domain.availableaddress.presentation.controller;

import com.project.dugoga.domain.availableaddress.application.dto.AvailableAddressCreateRequestDto;
import com.project.dugoga.domain.availableaddress.application.dto.AvailableAddressCreateResponseDto;
import com.project.dugoga.domain.availableaddress.application.service.AvailableAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/service-areas")
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
}