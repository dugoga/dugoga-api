package com.project.dugoga.domain.order.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "p_order_product")
public class OrderProduct {

    @Id
    private UUID id;
}
