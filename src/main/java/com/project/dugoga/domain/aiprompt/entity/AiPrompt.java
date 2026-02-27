package com.project.dugoga.domain.aiprompt.entity;

import com.project.dugoga.domain.product.entity.Product;
import com.project.dugoga.domain.store.entity.Store;
import com.project.dugoga.domain.user.entity.User;
import com.project.dugoga.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_ai_prompt")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiPrompt extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store storeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="product_id", nullable = false)
    private Product productId;

    @Column(name = "prompt_text", columnDefinition = "TEXT", nullable = false)
    private String promptText;

    @Column(name = "response_text", columnDefinition = "TEXT", nullable = false)
    private String responseText;

    @Builder
    public AiPrompt(User userId, Store storeId, Product productId, String promptText, String responseText) {
        this.userId = userId;
        this.storeId = storeId;
        this.productId = productId;
        this.promptText = promptText;
        this.responseText = responseText;
    }
}
