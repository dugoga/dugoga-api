package com.project.dugoga.domain.bookmark.application.dto;

import com.project.dugoga.domain.bookmark.domain.model.entity.Bookmark;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.store.domain.model.enums.StoreStatus;
import com.project.dugoga.global.dto.PageInfoDto;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class BookmarkListResponseDto {
    private List<BookmarkResponse> bookmarks;
    private PageInfoDto pageInfo;

    public static BookmarkListResponseDto of(Page<Bookmark> bookmarkPage) {
        List<BookmarkResponse> list = bookmarkPage.getContent().stream()
                .map(BookmarkResponse::from)
                .toList();

        return BookmarkListResponseDto.builder()
                .bookmarks(list)
                .pageInfo(PageInfoDto.from(bookmarkPage))
                .build();
    }

    @Getter
    @Builder
    public static class BookmarkResponse {
        private UUID id;
        private boolean isHidden;
        private StoreResponse store;


        public static BookmarkResponse from(Bookmark bookmark) {
            return BookmarkResponse.builder()
                    .id(bookmark.getId())
                    .isHidden(bookmark.isHidden())
                    .store(StoreResponse.from(bookmark.getStore()))
                    .build();
        }
    }

    @Getter
    @Builder
    public static class StoreResponse {
        private UUID id;
        private String name;
        private String comment;
        private StoreStatus status;

        public static StoreResponse from(Store store) {
            return StoreResponse.builder()
                    .id(store.getId())
                    .name(store.getName())
                    .comment(store.getComment())
                    .status(store.getStatus())
                    .build();
        }


    }
}


