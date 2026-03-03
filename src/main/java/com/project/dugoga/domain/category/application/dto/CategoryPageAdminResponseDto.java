package com.project.dugoga.domain.category.application.dto;

import com.project.dugoga.domain.category.domain.model.entity.Category;
import com.project.dugoga.global.dto.PageInfoDto;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class CategoryPageAdminResponseDto {

    private List<CategoryAdminResponseDto> categories;

    private PageInfoDto pageInfo;

    public static CategoryPageAdminResponseDto from(Page<Category> page) {
        return CategoryPageAdminResponseDto.builder()
                .categories(page.getContent()
                        .stream()
                        .map(CategoryAdminResponseDto::from)
                        .toList())
                .pageInfo(PageInfoDto.from(page))
                .build();
    }
}
