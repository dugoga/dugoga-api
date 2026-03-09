package com.project.dugoga.domain.bookmark.presentation.controller;

import com.project.dugoga.domain.bookmark.application.dto.BookmarkCreateResponseDto;
import com.project.dugoga.domain.bookmark.application.dto.BookmarkVisibilityUpdateRequestDto;
import com.project.dugoga.domain.bookmark.application.dto.BookmarkListResponseDto;
import com.project.dugoga.domain.bookmark.application.dto.BookmarkUpdateResponseDto;
import com.project.dugoga.domain.bookmark.application.service.BookmarkService;
import com.project.dugoga.global.security.jwt.CustomUserDetails;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BookmarkController {

    private final BookmarkService bookmarkService;


    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @PostMapping("/stores/{storeId}/bookmarks")
    public ResponseEntity<BookmarkCreateResponseDto>  createBookmark(@PathVariable UUID storeId,
                                                                     @AuthenticationPrincipal CustomUserDetails details) {
        BookmarkCreateResponseDto responseDto = bookmarkService.createBookmark(storeId, details.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PreAuthorize("hasAnyRole('MASTER', 'MANAGER')")
    @DeleteMapping("/stores/{storeId}/bookmarks")
    public ResponseEntity<Void> deleteBookmark(@PathVariable UUID storeId,
                                               @AuthenticationPrincipal CustomUserDetails userDetails) {

        bookmarkService.deleteBookmark(storeId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @PatchMapping("/bookmarks/visibility")
    public ResponseEntity<BookmarkUpdateResponseDto> updateBookmarkVisibility(
            @Valid @RequestBody BookmarkVisibilityUpdateRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(bookmarkService.visibilityUpdate(request, userDetails.getId()));
    }

    @PreAuthorize("hasAnyRole('CUSTOMER','MASTER','MANAGER')")
    @GetMapping("/bookmarks")
    public ResponseEntity<BookmarkListResponseDto> searchUserBookmarkList(
            Pageable pageable,
            @RequestParam(required = false) String query,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(bookmarkService.search(userDetails.getId(), userDetails.getUserRole(), query, pageable));
    }
}
