package com.project.dugoga.domain.availableaddress.infrastructure.repository;

import com.project.dugoga.domain.availableaddress.domain.model.entity.AvailableAddress;
import com.project.dugoga.domain.availableaddress.domain.repository.AvailableAddressRepository;
import com.project.dugoga.domain.availableaddress.infrastructure.repository.custom.AvailableAddressCustomRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AvailableAddressRepositoryImpl implements AvailableAddressRepository {

    private final AvailableAddressJpaRepository jpaRepository;
    private final AvailableAddressCustomRepository customRepository;

    @Override
    public boolean existsByRegion1depthNameAndRegion2depthName(String region1, String region2) {
        return jpaRepository.existsByRegion1depthNameAndRegion2depthName(region1, region2);
    }

    @Override
    public Optional<AvailableAddress> findByRegion1depthNameAndRegion2depthName(String region1, String region2) {
        return jpaRepository.findByRegion1depthNameAndRegion2depthName(region1, region2);
    }

    @Override
    public Optional<AvailableAddress> findByIdAndDeletedAtIsNull(UUID areaId) {
        return jpaRepository.findByIdAndDeletedAtIsNull(areaId);
    }

    @Override
    public Optional<AvailableAddress> findByIdAndDeletedAtIsNotNull(UUID areaId) {
        return jpaRepository.findByIdAndDeletedAtIsNotNull(areaId);
    }

    @Override
    public AvailableAddress save(AvailableAddress availableAddress) {
        return jpaRepository.save(availableAddress);
    }

    @Override
    public Page<AvailableAddress> search(String keyword, Pageable pageable) {
        return customRepository.search(keyword, pageable);
    }


}
