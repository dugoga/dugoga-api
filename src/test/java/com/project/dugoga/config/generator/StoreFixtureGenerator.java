package com.project.dugoga.config.generator;

import com.project.dugoga.domain.availableaddress.domain.model.entity.AvailableAddress;
import com.project.dugoga.domain.category.domain.model.entity.Category;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.user.domain.model.entity.User;

import java.time.LocalTime;

public class StoreFixtureGenerator {

    public static final String NAME = "피자킹 강남점";
    public static final String COMMENT = "피자 전문점";
    public static final String ADDRESS_NAME = "테헤란로 123";
    // 상수는 기본값용으로 남겨둘 수는 있지만, Store 생성 시에는 사용하지 않거나 아예 지워도 됩니다.
    public static final String REGION_3DEPTH = "역삼동";
    public static final String DETAIL_ADDRESS = "강남본점";
    public static final Double LONGITUDE = 127.0286;
    public static final Double LATITUDE = 37.4979;

    public static final LocalTime OPEN_AT = LocalTime.of(9, 0);   // 09:00:00
    public static final LocalTime CLOSE_AT = LocalTime.of(22, 0); // 22:00:00

    public static Store generateStoreFixture(
            User user,
            Category category,
            AvailableAddress availableAddress
    ) {
        return Store.of(
                user,
                category,
                availableAddress,
                NAME,
                COMMENT,
                ADDRESS_NAME,
                availableAddress.getRegion1depthName(),
                availableAddress.getRegion2depthName(),
                REGION_3DEPTH,
                DETAIL_ADDRESS,
                LONGITUDE,
                LATITUDE,
                OPEN_AT,
                CLOSE_AT
        );
    }

    // 커스텀용
    public static Store generateStoreFixture(
            User user, Category category, AvailableAddress availableAddress,
            String name, String addressName, String detailAddress
    ) {
        return Store.of(
                user,
                category,
                availableAddress,
                name,
                COMMENT,
                addressName,
                availableAddress.getRegion1depthName(),
                availableAddress.getRegion2depthName(),
                REGION_3DEPTH,
                detailAddress,
                LONGITUDE,
                LATITUDE,
                OPEN_AT,
                CLOSE_AT
        );
    }
}
