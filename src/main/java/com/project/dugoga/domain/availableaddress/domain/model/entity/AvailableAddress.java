package com.project.dugoga.domain.availableaddress.domain.model.entity;

import com.project.dugoga.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_available_address")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AvailableAddress extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "region_1depth_name", nullable = false)
    private String region1depthName;

    @Column(name = "region_2depth_name", nullable = false)
    private String region2depthName;


    private AvailableAddress(String region1depthName, String region2depthName) {
        this.region1depthName = region1depthName;
        this.region2depthName = region2depthName;
    }
}
