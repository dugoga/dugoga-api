package com.project.dugoga.config.config.generator;

import com.project.dugoga.domain.aiprompt.domain.model.entity.AiPrompt;
import com.project.dugoga.domain.product.domain.model.entity.Product;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.user.domain.model.entity.User;

public class AiPromptFixtureGenerator {
    public static final String PROMPT_TEXT = "맛있는 피자 설명 써줘";
    public static final String RESPONSE_TEXT = "치즈 풍미가 가득한 피자입니다.";

    public static AiPrompt generateAiPromptFixture(User user, Store store, Product product) {
        return AiPrompt.builder()
                .user(user)
                .store(store)
                .product(product)
                .promptText(PROMPT_TEXT)
                .responseText(RESPONSE_TEXT)
                .build();
    }
}
