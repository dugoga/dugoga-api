package com.project.dugoga.domain.bookmark.presentation.controller;

import com.project.dugoga.domain.bookmark.application.dto.BookmarkCreateResponseDto;
import com.project.dugoga.domain.bookmark.application.service.BookmarkService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    /*
    * todo : 권한 판단 - CONSUMER만 가능
    * */
    @PostMapping("/stores/{storeId}/bookmarks")
    public ResponseEntity<BookmarkCreateResponseDto>  createBookmark(@PathVariable UUID storeId) {
        // todo : 회원Id 가져오기
        Long userId = 1L;
        BookmarkCreateResponseDto responseDto = bookmarkService.createBookmark(storeId, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
