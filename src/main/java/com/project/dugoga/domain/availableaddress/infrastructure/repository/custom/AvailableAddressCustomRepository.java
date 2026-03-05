package com.project.dugoga.domain.availableaddress.infrastructure.repository.custom;


import com.project.dugoga.domain.availableaddress.domain.model.entity.AvailableAddress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AvailableAddressCustomRepository {
    Page<AvailableAddress> search(String keyword, Pageable pageable, Boolean isAdmin);
}
