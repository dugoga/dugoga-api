package com.project.dugoga.domain.bookmark.application.dto;

import com.project.dugoga.domain.bookmark.domain.model.entity.Bookmark;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.store.domain.model.enums.StoreStatus;
import com.project.dugoga.global.dto.PageInfoDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class AdminBookmarkListResponseDto {

    private List<BookmarkResponse> bookmarks;
    private PageInfoDto pageInfo;

    public static AdminBookmarkListResponseDto of(Page<Bookmark> bookmarkPage) {
        List<BookmarkResponse> list = bookmarkPage.getContent().stream()
                .map(BookmarkResponse::from)
                .toList();

        return AdminBookmarkListResponseDto.builder()
                .bookmarks(list)
                .pageInfo(PageInfoDto.from(bookmarkPage))
                .build();
    }

    @Getter
    @Builder
    private static class BookmarkResponse {
        private UUID id;
        private StoreResponse store;

        public static BookmarkResponse from(Bookmark bookmark) {
            return BookmarkResponse.builder()
                    .id(bookmark.getId())
                    .store(StoreResponse.from(bookmark.getStore()))
                    .build();
        }
    }

    @Getter
    @Builder
    private static class StoreResponse {
        private UUID id;
        private String name;
        private String comment;
        private StoreStatus status;
        private LocalDateTime deletedAt;

        public static StoreResponse from(Store store) {
            return StoreResponse.builder()
                    .id(store.getId())
                    .name(store.getName())
                    .comment(store.getComment())
                    .status(store.getStatus())
                    .deletedAt(store.getDeletedAt())
                    .build();
        }
    }
}
