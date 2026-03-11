package com.project.dugoga.domain.category.domain.model.entity;

import com.project.dugoga.global.entity.BaseEntity;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.UUID;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "p_category",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_category_name", columnNames = "name"),
                @UniqueConstraint(name = "uq_category_code", columnNames = "code")
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 20)
    private String code;

    @Column(nullable = false, length = 30)
    private String name;

    private Category(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Category of(String code, String name) {
        if(name == null || name.isBlank()){
            throw new BusinessException(ErrorCode.CATEGORY_NAME_REQUIRED);
        }
        if(code == null || code.isBlank()){
            throw new BusinessException(ErrorCode.CATEGORY_CODE_REQUIRED);
        }
        return new Category(code, name);
    }


    public void update(String code, String name) {

        if (this.isDeleted()) {
            throw new BusinessException(ErrorCode.CATEGORY_ALREADY_DELETED);
        }
        if(name == null || name.isBlank()){
            throw new BusinessException(ErrorCode.CATEGORY_NAME_REQUIRED);
        }
        if(code == null || code.isBlank()){
            throw new BusinessException(ErrorCode.CATEGORY_CODE_REQUIRED);
        }

        this.name = name;
        this.code = code;
    }

    public void delete(Long userId) {
        if (this.isDeleted()) {
            throw new BusinessException(ErrorCode.CATEGORY_ALREADY_DELETED);
        }
        this.softDelete(userId);
    }
}
