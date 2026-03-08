package com.project.dugoga.domain.availableaddress.infrastructure.repository;

import com.project.dugoga.domain.availableaddress.domain.model.entity.AvailableAddress;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvailableAddressJpaRepository extends JpaRepository<AvailableAddress, UUID> {

    boolean existsByRegion1depthNameAndRegion2depthName(String region1, String region2);

    Optional<AvailableAddress> findByRegion1depthNameAndRegion2depthName(String region1, String region2);

    Optional<AvailableAddress> findByIdAndDeletedAtIsNull(UUID areaId);
}
