package com.project.dugoga.domain.category.application.dto;

import com.project.dugoga.domain.category.domain.model.entity.Category;
import com.project.dugoga.global.dto.PageInfoDto;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Builder(access = AccessLevel.PRIVATE)
@Getter
public class CategoryPageResponseDto {

    private List<CategoryResponseDto> categories;
    private PageInfoDto pageInfo;



    public static CategoryPageResponseDto from(Page<Category> page) {

        List<CategoryResponseDto> content = page.getContent()
                .stream().map(CategoryResponseDto::from).toList();

        return CategoryPageResponseDto.builder()
                .categories(content)
                .pageInfo(PageInfoDto.from(page))
                .build();

    }
}
