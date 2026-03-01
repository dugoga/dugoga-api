package com.project.dugoga.domain.availableaddress.application.domain.repository;

import com.project.dugoga.domain.availableaddress.application.domain.model.entity.AvailableAddress;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailableAddressRepository extends JpaRepository<AvailableAddress, UUID> {
    boolean existsByRegion1depthNameAndRegion2depthName(String region1, String region2);

}
