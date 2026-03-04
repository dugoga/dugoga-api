package com.project.dugoga.domain.availableaddress.application.dto;

import com.project.dugoga.domain.availableaddress.domain.model.entity.AvailableAddress;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AvailableAddressUpdateResponseDto {

    private UUID id;
    private LocalDateTime updatedAt;



    public static AvailableAddressUpdateResponseDto from(AvailableAddress availableAddress) {
        return new AvailableAddressUpdateResponseDto(availableAddress.getId(), availableAddress.getUpdatedAt());

    }
}
