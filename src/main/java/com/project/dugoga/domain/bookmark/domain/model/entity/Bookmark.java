package com.project.dugoga.domain.bookmark.domain.model.entity;

import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.global.entity.BaseEntity;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "p_bookmark",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_p_bookmark_store_user",
                        columnNames = {"store_id", "user_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Bookmark extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    @Column(name = "is_hidden", nullable = false)
    private boolean isHidden = false;

    public static Bookmark of(User user, Store store) {
        if (user == null || store == null) {
            throw new IllegalArgumentException("user와 store는 필수입니다.");
        }

            return Bookmark.builder()
                    .user(user)
                .store(store)
                .isHidden(false)
                .build();
    }

    public void delete(Long userId) {

        if (this.isDeleted()) {
            throw new BusinessException(ErrorCode.BOOKMARK_ALREADY_DELETED);
        }

        this.softDelete(userId);
    }


    public void updateVisibility(boolean isHidden) {
        if (this.isDeleted()) {
            throw new BusinessException(ErrorCode.BOOKMARK_ALREADY_DELETED);
        }

        if (this.isHidden == isHidden) {
            throw new BusinessException(ErrorCode.BOOKMARK_VISIBILITY_UNCHANGED);
        }

        this.isHidden = isHidden;
    }
}
