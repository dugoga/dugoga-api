package com.project.dugoga.domain.bookmark.application.dto;

import com.project.dugoga.domain.bookmark.domain.model.entity.Bookmark;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
public class BookmarkUpdateResponseDto {

    private UUID id;
    private LocalDateTime updatedAt;

    public static BookmarkUpdateResponseDto from(Bookmark bookmark) {
        return BookmarkUpdateResponseDto.builder()
                .id(bookmark.getId())
                .updatedAt(bookmark.getUpdatedAt())
                .build();
    }
}
