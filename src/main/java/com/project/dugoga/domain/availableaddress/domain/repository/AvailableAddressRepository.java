package com.project.dugoga.domain.availableaddress.domain.repository;

import com.project.dugoga.domain.availableaddress.domain.model.entity.AvailableAddress;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailableAddressRepository {
    boolean existsByRegion1depthNameAndRegion2depthName(String region1, String region2);

    Optional<AvailableAddress> findByRegion1depthNameAndRegion2depthName(String region1, String region2);

    Optional<AvailableAddress> findByIdAndDeletedAtIsNull(UUID areaId);

    Optional<AvailableAddress> findByIdAndDeletedAtIsNotNull(UUID areaId);

    AvailableAddress save(AvailableAddress availableAddress);

    Page<AvailableAddress> search(String keyword, Pageable normalizePageable, Boolean isAdmin);
}
