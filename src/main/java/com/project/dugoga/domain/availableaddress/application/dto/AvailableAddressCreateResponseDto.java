package com.project.dugoga.domain.availableaddress.application.dto;

import com.project.dugoga.domain.availableaddress.application.domain.model.entity.AvailableAddress;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;

@Getter
public class AvailableAddressCreateResponseDto {

    private final UUID id;

    private final LocalDateTime createdAt;

    public AvailableAddressCreateResponseDto(UUID id, LocalDateTime createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    public static AvailableAddressCreateResponseDto from(AvailableAddress saved) {
        return new AvailableAddressCreateResponseDto(saved.getId(), saved.getCreatedAt());
    }
}
