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

    @Column(nullable = false)
    private String region1DepthName;

    @Column(nullable = false)
    private String region2DepthName;

    @Column(nullable = false)
    private String region3DepthName;

    private AvailableAddress(String region1DepthName, String region2DepthName, String region3DepthName) {
        this.region1DepthName = region1DepthName;
        this.region2DepthName = region2DepthName;
        this.region3DepthName = region3DepthName;
    }
}
