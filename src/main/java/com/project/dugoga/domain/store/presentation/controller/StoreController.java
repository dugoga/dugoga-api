package com.project.dugoga.domain.store.presentation.controller;

import com.project.dugoga.domain.store.application.dto.StoreCreateRequestDto;
import com.project.dugoga.domain.store.application.dto.StoreCreateResponseDto;
import com.project.dugoga.domain.store.application.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class StoreController {
    private final StoreService storeService;

    @PostMapping
    public ResponseEntity<StoreCreateResponseDto> createStore(
            @Valid @RequestBody StoreCreateRequestDto request
    ) {
        StoreCreateResponseDto responseDto = storeService.createStore(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
