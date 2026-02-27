package com.project.dugoga.domain.review.entity;

import com.project.dugoga.domain.order.entity.Order;
import com.project.dugoga.domain.store.entity.Store;
import com.project.dugoga.domain.user.entity.User;
import com.project.dugoga.global.entity.BaseEntity;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store storeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order orderId;

    @Column(nullable = false)
    private Integer rating;

    @Column
    private String content;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_hidden", nullable = false)
    private Boolean isHidden;

    @Builder
    public Review(Store storeId, User userId, Order orderId, Integer rating, String content, String imageUrl, Boolean isHidden) {
        // 평점 범위(1~5) 검증
        if (rating == null || rating < 1 || rating > 5) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "올바른 평점 값이 아닙니다.");
        }

        this.storeId = storeId;
        this.userId = userId;
        this.orderId = orderId;
        this.rating = rating;
        this.content = content;
        this.imageUrl = imageUrl;
        this.isHidden = isHidden;
    }
}
