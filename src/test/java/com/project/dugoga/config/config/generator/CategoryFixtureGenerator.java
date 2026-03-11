package com.project.dugoga.config.config.generator;

import com.project.dugoga.domain.category.domain.model.entity.Category;

public class CategoryFixtureGenerator {

    public static final String CODE = "PIZ";
    public static final String NAME = "피자";

    public static Category generateCategoryFixture() {
        return Category.of(
                CODE,
                NAME
        );
    }

    // 커스텀용
    public static Category generateCategoryFixture(String code, String name) {
        return Category.of(
                code,
                name
        );
    }
}
