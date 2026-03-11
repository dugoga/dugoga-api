package com.project.dugoga.config.config.generator;

import com.project.dugoga.domain.product.domain.model.entity.Product;
import com.project.dugoga.domain.store.domain.model.entity.Store;

public class ProductFixtureGenerator {

    public static final String NAME = "치즈피자";
    public static final String COMMENT = "치즈향 가득한 피자입니다.";
    public static final Integer PRICE = 25000;
    public static final String IMAGE_URL = "../../../";

    public static Product generateProductFixture(Store store) {
        return Product.create(
                store,
                NAME,
                COMMENT,
                PRICE,
                IMAGE_URL
        );
    }

    // 커스텀용
    public static Product generateProductFixture(
            Store store,
            String name,
            String comment,
            Integer price,
            String imageUrl
    ) {
        return Product.create(
                store,
                name,
                comment,
                price,
                imageUrl
        );
    }
}
