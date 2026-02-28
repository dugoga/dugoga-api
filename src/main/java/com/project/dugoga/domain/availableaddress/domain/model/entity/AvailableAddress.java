package com.project.dugoga.domain.availableaddress.domain.model.entity;

import com.project.dugoga.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.UUID;

@Entity
public class AvailableAddress extends BaseEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String region_1depth_name;

    @Column(nullable = false)
    private String region_2depth_name;

    @Column(nullable = false)
    private String region_3depth_name;


}
