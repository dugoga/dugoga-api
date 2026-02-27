package com.project.dugoga.domain.store.entity;

import com.project.dugoga.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "p_store")
@Getter
@NoArgsConstructor
public class Store extends BaseEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;
}