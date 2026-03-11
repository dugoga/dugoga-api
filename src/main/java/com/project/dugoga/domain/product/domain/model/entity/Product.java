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
    private Product(
            Store store,
            String name,
            String comment,
            Integer price,
            String imageUrl,
            Boolean isSoldOut,
            Boolean isHidden
    ) {
        this.store = store;
        this.name = name;
        this.comment = comment;
        this.price = price;
        this.imageUrl = imageUrl;
        this.isSoldOut = isSoldOut;
        this.isHidden = isHidden;
    }

    public static Product create(
            Store store,
            String name,
            String comment,
            Integer price,
            String imageUrl
    ) {
        Product product = Product.builder()
                .store(store)
                .name(name)
                .comment(comment)
                .price(price)
                .imageUrl(imageUrl)
                .isSoldOut(false)
                .isHidden(false)
                .build();

        if (store != null) {
            store.addProduct(product);
        }

        return product;
    }

    public void validateOrderable() {
        if (this.isDeleted() || this.isHidden) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        if (this.isSoldOut) {
            throw new BusinessException(ErrorCode.PRODUCT_SOLD_OUT);
        }
    }

    public void delete(Long userId) {
        if (this.isDeleted()) {
            throw new BusinessException(ErrorCode.PRODUCT_ALREADY_DELETED);
        }
        this.softDelete(userId);
    }

    public void update(String name,
                       String comment,
                       Integer price,
                       String imageUrl
    ) {
        this.name = name;
        this.comment = comment;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public void updateIsHidden(Boolean isHidden) {
        this.isHidden = isHidden;
    }

    public void updateIsSoldOut(Boolean isSoldOut) {
        this.isSoldOut = isSoldOut;
    }
}
