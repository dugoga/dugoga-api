package com.project.dugoga.domain.user.application.dto;

import com.project.dugoga.global.dto.PageInfoDto;
import org.springframework.data.domain.Page;

import java.util.List;

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
