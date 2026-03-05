package com.project.dugoga.domain.availableaddress.domain.repository;

import com.project.dugoga.domain.availableaddress.domain.model.entity.AvailableAddress;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailableAddressRepository extends JpaRepository<AvailableAddress, UUID> {
    boolean existsByRegion1depthNameAndRegion2depthName(String region1, String region2);

    Optional<AvailableAddress> findByRegion1depthNameAndRegion2depthName(String region1, String region2);

    Optional<AvailableAddress> findByIdAndDeletedAtIsNull(UUID areaId);

    Optional<AvailableAddress> findByIdAndDeletedAtIsNotNull(UUID areaId);

    Page<AvailableAddress> findAllByRegion1depthNameAndRegion2depthNameContainingAndDeletedAtIsNull(String keyword, String name2, Pageable normalizePageable);

    Page<AvailableAddress> findAllByDeletedAtIsNull(Pageable normalizePageable);
}
