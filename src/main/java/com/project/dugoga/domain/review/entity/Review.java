package com.project.dugoga.domain.review.entity;

import com.project.dugoga.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "store_id", nullable = false)
    private UUID storeId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "order_id", nullable = false, unique = true)
    private UUID orderId;

    @Column(nullable = false)
    private Integer rating;

    @Column
    private String content;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_hidden", nullable = false)
    private Boolean isHidden;

    @Builder
    public Review(UUID storeId, Long userId, UUID orderId, Integer rating, String content, String imageUrl, Boolean isHidden) {
        this.storeId = storeId;
        this.userId = userId;
        this.orderId = orderId;
        this.rating = rating;
        this.content = content;
        this.imageUrl = imageUrl;
        this.isHidden = isHidden;
    }
}