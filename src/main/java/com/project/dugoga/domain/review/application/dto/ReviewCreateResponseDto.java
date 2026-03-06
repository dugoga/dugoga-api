package com.project.dugoga.domain.review.application.dto;

import com.project.dugoga.domain.review.domain.model.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class ReviewCreateResponseDto {

    private UUID id;
    private LocalDateTime createdAt;

    public static ReviewCreateResponseDto from(Review review){
        return ReviewCreateResponseDto.builder()
                .id(review.getId())
                .createdAt(review.getCreatedAt())
                .build();
    }

}
