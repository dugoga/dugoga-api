package com.project.dugoga.domain.store.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StoreStatus {
    OPEN("OPEN"),
    CLOSED("CLOSED"),
    PREPARING("PREPARING");

    private final String description;
}