package com.project.dugoga.global.config.generator;

import com.project.dugoga.domain.availableaddress.domain.model.entity.AvailableAddress;

public class AvailableAddressFixtureGenerator {

    public static final String REGION_1DEPTH_NAME = "서울특별시";
    public static final String REGION_2DEPTH_NAME = "강남구";

    public static AvailableAddress generateAvailableAddressFixture() {
        return AvailableAddress.of(
                REGION_1DEPTH_NAME,
                REGION_2DEPTH_NAME
        );
    }

    // 커스텀용
    public static AvailableAddress generateAvailableAddressFixture(
            String region1depthName,
            String region2depthName
    ) {
        return AvailableAddress.of(
                region1depthName,
                region2depthName
        );
    }
}