package com.project.dugoga.config.generator;

import com.project.dugoga.domain.order.domain.model.entity.Order;
import com.project.dugoga.domain.review.domain.model.entity.Review;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.user.domain.model.entity.User;

public class ReviewFixtureGenerator {

    public static final Integer RATING = 4;
    public static final String CONTENT = "치즈향이 풍부하고 맛있네요";
    public static final String IMAGE_URL = "../../../";
    public static final Boolean IS_HIDDEN = false;

    public static Review generateReviewFixture(User user, Store store, Order order) {
        return Review.builder()
                .userId(user)
                .storeId(store)
                .orderId(order)
                .rating(RATING)
                .content(CONTENT)
                .imageUrl(IMAGE_URL)
                .isHidden(IS_HIDDEN)
                .build();
    }

    // 커스텀용
    public static Review generateReviewFixture(
            User user, Store store, Order order, Integer rating,
            String content, String imageUrl, Boolean isHidden)
    {
        return Review.builder()
                .userId(user)
                .storeId(store)
                .orderId(order)
                .rating(rating)
                .content(content)
                .imageUrl(imageUrl)
                .isHidden(isHidden)
                .build();
    }
}
