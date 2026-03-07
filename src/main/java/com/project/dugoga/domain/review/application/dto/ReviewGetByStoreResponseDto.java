package com.project.dugoga.domain.review.application.dto;

import com.project.dugoga.domain.review.domain.model.entity.Review;
import com.project.dugoga.global.dto.PageInfoDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class ReviewGetByStoreResponseDto {

    private List<ReviewGetDetailResponseDto> reviewList;
    private PageInfoDto pageInfo;

    public static ReviewGetByStoreResponseDto from(Page<Review> page) {
        List<ReviewGetDetailResponseDto> content = page.getContent()
                .stream().map(ReviewGetDetailResponseDto::from).toList();

        return ReviewGetByStoreResponseDto.builder()
                .reviewList(content)
                .pageInfo(PageInfoDto.from(page))
                .build();
    }
}
