package com.project.dugoga.global.dto;

import com.project.dugoga.domain.category.domain.model.entity.Category;
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


    public static PageInfoDto from(Page<Category> categories) {
        return PageInfoDto.builder()
                .page(categories.getNumber())
                .size(categories.getSize())
                .totalElements(categories.getTotalElements())
                .totalPages(categories.getTotalPages())
                .build();
    }
}
