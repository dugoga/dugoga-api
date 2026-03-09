package com.project.dugoga.domain.store.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class StoreSearchCondDto {
    private String search;
    private String category;
}
