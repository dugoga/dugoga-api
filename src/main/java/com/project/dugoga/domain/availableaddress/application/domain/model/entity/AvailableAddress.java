package com.project.dugoga.domain.availableaddress.application.domain.model.entity;

import com.project.dugoga.domain.availableaddress.application.dto.AvailableAddressCreateRequestDto;
import com.project.dugoga.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "p_available_address",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_p_available_address_region_1depth_name_region_2depth_name",
                        columnNames = {"region_1depth_name","region_2depth_name"}
                )
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AvailableAddress extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "region_1depth_name", nullable = false)
    private String region1depthName;

    @Column(name = "region_2depth_name", nullable = false)
    private String region2depthName;


    public AvailableAddress(String region1depthName, String region2depthName) {
        this.region1depthName = region1depthName;
        this.region2depthName = region2depthName;
    }

}
