package com.project.dugoga.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
public class PageResponseDto<T> {
    private final List<T> content;
    private final PageInfoDto pageInfo;

    private PageResponseDto(Page<T> page) {
        this.content = page.getContent();
        this.pageInfo = PageInfoDto.from(page);
    }

    public static <T> PageResponseDto<T> from(Page<T> page) {
        return new PageResponseDto<>(page);
    }
}
