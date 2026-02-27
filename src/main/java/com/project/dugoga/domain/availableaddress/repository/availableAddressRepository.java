package com.project.dugoga.domain.availableaddress.repository;

import com.project.dugoga.domain.availableaddress.entity.AvailableAddress;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface availableAddressRepository extends JpaRepository<AvailableAddress, UUID> {
}
