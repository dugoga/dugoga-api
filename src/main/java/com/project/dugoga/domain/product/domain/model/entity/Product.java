package com.project.dugoga.domain.product.domain.model.entity;

import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.global.entity.BaseEntity;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_product")
@Getter
@NoArgsConstructor
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(nullable = false)
    private String name;

    private String comment;

    @Column(nullable = false)
    private Integer price;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_sold_out", nullable = false)
    private Boolean isSoldOut;

    @Column(name = "is_hidden", nullable = false)
    private Boolean isHidden;

    @Builder
    private Product(Store store, String name, String comment, Integer price,
                    String imageUrl, Boolean isSoldOut, Boolean isHidden) {

        this.store = store;
        this.name = name;
        this.comment = comment;
        this.price = price;
        this.imageUrl = imageUrl;

        // 기본값 설정
        this.isSoldOut = (isSoldOut != null) ? isSoldOut : false;
        this.isHidden = (isHidden != null) ? isHidden : false;
    }

    public void validateOrderable() {
        if (this.isDeleted() || this.isHidden) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        if (this.isSoldOut) {
            throw new BusinessException(ErrorCode.PRODUCT_SOLD_OUT);
        }
    }
}
