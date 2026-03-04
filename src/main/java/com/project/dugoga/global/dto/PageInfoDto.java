package com.project.dugoga.global.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class PageInfoDto {

    private int page;
    private int size;
    private Long totalElements;
    private int totalPages;


    public static PageInfoDto from(Page<?> page) {
        return PageInfoDto.builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
