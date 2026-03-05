package com.project.dugoga.domain.availableaddress.infrastructure.repository;

import com.project.dugoga.domain.availableaddress.domain.model.entity.AvailableAddress;
import com.project.dugoga.domain.availableaddress.domain.repository.AvailableAddressRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class AvailableAddressRepositoryImpl implements AvailableAddressRepository {

    private final AvailableAddressJpaRepository jpaRepository;

    @Override
    public boolean existsByRegion1depthNameAndRegion2depthName(String region1, String region2) {
        return false;
    }

    @Override
    public Optional<AvailableAddress> findByRegion1depthNameAndRegion2depthName(String region1, String region2) {
        return Optional.empty();
    }

    @Override
    public Optional<AvailableAddress> findByIdAndDeletedAtIsNull(UUID areaId) {
        return Optional.empty();
    }

    @Override
    public Optional<AvailableAddress> findByIdAndDeletedAtIsNotNull(UUID areaId) {
        return Optional.empty();
    }

    @Override
    public Page<AvailableAddress> findAllByRegion1depthNameAndRegion2depthNameContainingAndDeletedAtIsNull(
            String keyword, String name2, Pageable normalizePageable) {
        return null;
    }

    @Override
    public Page<AvailableAddress> findAllByDeletedAtIsNull(Pageable normalizePageable) {
        return null;
    }

    @Override
    public AvailableAddress save(AvailableAddress availableAddress) {
        return null;
    }
}
