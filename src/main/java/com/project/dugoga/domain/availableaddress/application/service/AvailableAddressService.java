package com.project.dugoga.domain.availableaddress.application.service;

import com.project.dugoga.domain.availableaddress.application.dto.AvailableAddressCreateRequestDto;
import com.project.dugoga.domain.availableaddress.application.dto.AvailableAddressCreateResponseDto;
import com.project.dugoga.domain.availableaddress.domain.model.entity.AvailableAddress;
import com.project.dugoga.domain.availableaddress.domain.repository.AvailableAddressRepository;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AvailableAddressService {

    private final AvailableAddressRepository availableAddressRepository;

    public AvailableAddressCreateResponseDto createAvailableAddress(AvailableAddressCreateRequestDto dto) {

        String region1 = dto.getRegion1depthName().trim();
        String region2 = dto.getRegion2depthName().trim();

        if(availableAddressRepository.existsByRegion1depthNameAndRegion2depthName(region1, region2)){
            throw new BusinessException(ErrorCode.AVAILABLE_ADDRESS_ALREADY_EXISTS);
        }

        AvailableAddress availableAddress = new AvailableAddress(region1, region2);

        AvailableAddress saved = availableAddressRepository.save(availableAddress);

        return AvailableAddressCreateResponseDto.from(saved);
    }
}
