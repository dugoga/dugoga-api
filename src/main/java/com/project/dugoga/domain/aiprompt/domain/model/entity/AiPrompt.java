package com.project.dugoga.domain.aiprompt.domain.model.entity;

import com.project.dugoga.domain.product.domain.model.entity.Product;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.user.domain.model.entity.User;
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
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="product_id", nullable = false)
    private Product product;

    @Column(name = "prompt_text", columnDefinition = "TEXT", nullable = false)
    private String promptText;

    @Column(name = "response_text", columnDefinition = "TEXT", nullable = false)
    private String responseText;

    @Builder
    public AiPrompt(User user, Store store, Product product, String promptText, String responseText) {
        this.user = user;
        this.store = store;
        this.product = product;
        this.promptText = promptText;
        this.responseText = responseText;
    }

    public void updateAiPrompt(String newPrompt, String newResponse) {
        this.promptText = newPrompt;
        this.responseText = newResponse;
    }

    public void delete(Long userId) {
        this.softDelete(userId);
    }

    public void restore() {
        this.restoreDelete();
    }
}
