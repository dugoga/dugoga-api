package com.project.dugoga.domain.store.domain.model.entity;

import com.project.dugoga.domain.availableaddress.domain.model.entity.AvailableAddress;
import com.project.dugoga.domain.category.domain.model.entity.Category;
import com.project.dugoga.domain.product.domain.model.entity.Product;
import com.project.dugoga.domain.store.domain.model.enums.StoreStatus;
import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.global.entity.BaseEntity;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_store")
@Getter
@NoArgsConstructor
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

    // 객체 지향적 설계: 연관 관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "available_address_id", nullable = false)
    private AvailableAddress availableAddress;

    @Column(name = "category_code", nullable = false)
    private String categoryCode;

    @OneToMany(mappedBy = "store")
    private List<Product> products = new ArrayList<>();

    @Column(nullable = false)
    private String name;

    private String comment;

    @Column(name = "address_name", nullable = false)
    private String addressName;

    @Column(name = "region_1depth_name", nullable = false)
    private String region1depthName;

    @Column(name = "region_2depth_name", nullable = false)
    private String region2depthName;

    @Column(name = "region_3depth_name", nullable = false)
    private String region3depthName;

    @Column(name = "detail_address", nullable = false)
    private String detailAddress;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Double latitude;

    @Column(name = "is_hidden", nullable = false)
    private Boolean isHidden;

    @Column(name = "open_at", nullable = false)
    private LocalTime openAt;

    @Column(name = "close_at", nullable = false)
    private LocalTime closeAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StoreStatus status;

    @Column(name = "review_count", nullable = false)
    private Long reviewCount;

    @Column(name = "average_rating")
    private Double averageRating;

    @Builder    // 테스트의 용의성을 위해 private 미적용
    private Store(User user, Category category, AvailableAddress availableAddressId,
                  String name, String comment, String addressName, String region1depthName,
                  String region2depthName, String region3depthName, String detailAddress,
                  Double longitude, Double latitude, Boolean isHidden, LocalTime openAt,
                  LocalTime closeAt, StoreStatus status, Long reviewCount, Double averageRating) {
        validateOperatingHours(openAt, closeAt);

        this.user = user;
        this.category = category;
        this.availableAddress = availableAddressId;
        this.categoryCode = category.getCode();
        this.name = name;
        this.comment = comment;
        this.addressName = addressName;
        this.region1depthName = region1depthName;
        this.region2depthName = region2depthName;
        this.region3depthName = region3depthName;
        this.detailAddress = detailAddress;
        this.longitude = longitude;
        this.latitude = latitude;
        this.openAt = openAt;
        this.closeAt = closeAt;

        // 기본값 로직
        this.isHidden = (isHidden != null) ? isHidden : false;
        this.status = (status != null) ? status : StoreStatus.PREPARING;
        this.reviewCount = (reviewCount != null) ? reviewCount : 0L;
        this.averageRating = averageRating;
    }

    public static Store of(User user, Category category, String name, String comment,
                           String addressName, String region1depthName, String region2depthName, String region3depthName, String detailAddress,
                           Double longitude, Double latitude,
                           LocalTime openAt, LocalTime closeAt) {
        return Store.builder()
                .user(user)
                .category(category)
                .name(name)
                .comment(comment)
                .addressName(addressName)
                .region1depthName(region1depthName)
                .region2depthName(region2depthName)
                .region3depthName(region3depthName)
                .detailAddress(detailAddress)
                .longitude(longitude)
                .latitude(latitude)
                .openAt(openAt)
                .closeAt(closeAt)
                .build();
    }

    public void update(Category category, String name, String comment,
                       String addressName, String region1depthName, String region2depthName, String region3depthName,
                       String detailAddress, Double longitude, Double latitude,
                       LocalTime openAt, LocalTime closeAt) {
        validateOperatingHours(openAt, closeAt);

        this.category = category;
        this.categoryCode = category.getCode();
        this.name = name;
        this.comment = comment;
        this.addressName = addressName;
        this.region1depthName = region1depthName;
        this.region2depthName = region2depthName;
        this.region3depthName = region3depthName;
        this.detailAddress = detailAddress;
        this.longitude = longitude;
        this.latitude = latitude;
        this.openAt = openAt;
        this.closeAt = closeAt;
    }

    public void updateVisibility(Boolean isHidden) {
        this.isHidden = isHidden;
    }

    public void updateStatus(StoreStatus status) {
        this.status = status;
    }

    public void delete(Long userId){
        if(this.isDeleted()){
            throw new BusinessException(ErrorCode.STORE_ALREADY_DELETED);
        }
        this.products.forEach(product -> product.delete(userId));
        this.softDelete(userId);
    }


    private void validateOperatingHours(LocalTime openAt, LocalTime closeAt) {
        if (openAt == null || closeAt == null) return;
        if (!closeAt.isAfter(openAt)) {
            throw new BusinessException(ErrorCode.STORE_INVALID_OPERATING_HOURS);
        }
    }

    public void validateOrderable() {
        if (this.isDeleted() || this.isHidden) {
            throw new BusinessException(ErrorCode.STORE_NOT_FOUND);
        }
        if (!this.isOpen()) {
            throw new BusinessException(ErrorCode.STORE_NOT_OPEN);
        }
    }

    private Boolean isOpen() {
        return this.status == StoreStatus.OPEN;
    }

    public void validateOwner(Long userId) {
        if(!this.user.getId().equals(userId)) {
            throw new BusinessException(ErrorCode.STORE_NOT_OWNER);
        }
    }
}
