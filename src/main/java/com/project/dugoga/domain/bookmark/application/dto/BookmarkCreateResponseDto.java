package com.project.dugoga.domain.bookmark.application.dto;

import com.project.dugoga.domain.bookmark.domain.model.entity.Bookmark;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;

@Getter
public class BookmarkCreateResponseDto {
    private final UUID id;
    private final LocalDateTime createdAt;


    private BookmarkCreateResponseDto(UUID id, LocalDateTime createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    public static BookmarkCreateResponseDto from(Bookmark bookmark) {
        return new BookmarkCreateResponseDto(bookmark.getId(), bookmark.getCreatedAt());
    }
}
