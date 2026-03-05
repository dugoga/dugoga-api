package com.project.dugoga.domain.availableaddress.infrastructure.repository;

import com.project.dugoga.domain.availableaddress.domain.model.entity.AvailableAddress;
import com.project.dugoga.domain.availableaddress.infrastructure.repository.custom.AvailableAddressCustomRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvailableAddressJpaRepository extends JpaRepository<AvailableAddress, UUID>,
        AvailableAddressCustomRepository {

}
