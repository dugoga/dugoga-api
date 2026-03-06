package com.project.dugoga.domain.availableaddress.application.service;

import com.project.dugoga.domain.availableaddress.application.dto.AvailableAddressCreateRequestDto;
import com.project.dugoga.domain.availableaddress.application.dto.AvailableAddressCreateResponseDto;
import com.project.dugoga.domain.availableaddress.application.dto.AvailableAddressUpdateRequestDto;
import com.project.dugoga.domain.availableaddress.application.dto.AvailableAddressUpdateResponseDto;
import com.project.dugoga.domain.availableaddress.application.dto.AvailableAddressListDto;
import com.project.dugoga.domain.availableaddress.domain.model.entity.AvailableAddress;
import com.project.dugoga.domain.availableaddress.domain.repository.AvailableAddressRepository;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AvailableAddressService {

    private final AvailableAddressRepository availableAddressRepository;

    @Transactional
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

    @Transactional
    public AvailableAddressUpdateResponseDto updateAvailableAddress(UUID areaId,
                                                                    AvailableAddressUpdateRequestDto request) {
        String region1 = request.getRegion1depthName().trim();
        String region2 = request.getRegion2depthName().trim();

        AvailableAddress availableAddress = availableAddressRepository.findByIdAndDeletedAtIsNull(areaId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AVAILABLE_ADDRESS_NOT_FOUND));


        if (availableAddress.getRegion1depthName().equals(region1) &&
                availableAddress.getRegion2depthName().equals(region2)) {
            throw new BusinessException(ErrorCode.AVAILABLE_ADDRESS_ALREADY_EXISTS);
        }

        availableAddress.update(region1, region2);

        return AvailableAddressUpdateResponseDto.from(availableAddress);

    }

    @Transactional
    public void deleteAvailableAddress(UUID areaId, Long userId) {
        AvailableAddress availableAddress = availableAddressRepository.findByIdAndDeletedAtIsNull(areaId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AVAILABLE_ADDRESS_NOT_FOUND));

        availableAddress.delete(userId);
    }

    @Transactional
    public AvailableAddressUpdateResponseDto restore(UUID areaId) {
        AvailableAddress availableAddress = availableAddressRepository.findByIdAndDeletedAtIsNotNull(areaId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AVAILABLE_ADDRESS_NOT_FOUND));
        availableAddress.restore();

        return AvailableAddressUpdateResponseDto.from(availableAddress);
    }

    public AvailableAddressListDto searchAvailableAddress(Pageable pageable, String query) {
        return AvailableAddressListDto.of(availableAddressRepository.search(query, pageable));
    }
}
