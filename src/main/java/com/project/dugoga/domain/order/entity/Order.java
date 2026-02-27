package com.project.dugoga.domain.order.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "p_order")
public class Order {

    @Id
    private UUID id;
}
