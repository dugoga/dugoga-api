package com.project.dugoga.domain.category.domain.model.entity;

import com.project.dugoga.global.entity.BaseEntity;
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
                @UniqueConstraint(name = "uq_category_name_code", columnNames = "name"),
                @UniqueConstraint(name = "uq_category_code", columnNames = "code")
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, length = 20)
    private String code;

    private Category(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public static Category create(String name, String code) {
        if(name == null || name.isBlank() || code == null || code.isBlank()){
            throw new IllegalArgumentException("카테고리의 이름과 코드는 필수입니다.");
        }

        name = name.trim();
        code = code.trim().toUpperCase();


        return new Category(name, code);
    }
}
