package com.project.dugoga.domain.bookmark.presentation.controller;

import com.project.dugoga.domain.bookmark.application.dto.BookmarkCreateResponseDto;
import com.project.dugoga.domain.bookmark.application.dto.BookmarkVisibilityUpdateRequestDto;
import com.project.dugoga.domain.bookmark.application.dto.BookmarkListResponseDto;
import com.project.dugoga.domain.bookmark.application.dto.BookmarkUpdateResponseDto;
import com.project.dugoga.domain.bookmark.application.service.BookmarkService;
import com.project.dugoga.global.security.jwt.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "즐겨찾기", description = "즐겨찾기 관련 API")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Operation(
            summary = "즐겨찾기 등록",
            description = "즐겨찾기를 등록합니다. 역할이 'CUSTOMER' 권한을 가진 사용자만 접근 가능합니다."
    )
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @PostMapping("/stores/{storeId}/bookmarks")
    public ResponseEntity<BookmarkCreateResponseDto>  createBookmark(@PathVariable UUID storeId,
                                                                     @AuthenticationPrincipal CustomUserDetails details) {
        BookmarkCreateResponseDto responseDto = bookmarkService.createBookmark(storeId, details.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(
            summary = "즐겨찾기 삭제",
            description = "즐겨찾기를 삭제합니다. 역할이 'MASTER' 또는 'MANAGER' 권한을 가진 사용자만 접근 가능합니다."
    )
    @PreAuthorize("hasAnyRole('MASTER', 'MANAGER')")
    @DeleteMapping("/stores/{storeId}/bookmarks")
    public ResponseEntity<Void> deleteBookmark(@PathVariable UUID storeId,
                                               @AuthenticationPrincipal CustomUserDetails userDetails) {

        bookmarkService.deleteBookmark(storeId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "즐겨찾기 숨김처리 수정",
            description = "즐겨찾기를 숨김처리 여부를 수정합니다. 역할이 'CUSTOMER' 권한을 가진 사용자만 접근 가능합니다."
    )
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @PatchMapping("/bookmarks/visibility")
    public ResponseEntity<BookmarkUpdateResponseDto> updateBookmarkVisibility(
            @Valid @RequestBody BookmarkVisibilityUpdateRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(bookmarkService.visibilityUpdate(request, userDetails.getId()));
    }

    @Operation(
            summary = "즐겨찾기 조회",
            description = "사용자의 즐겨찾기 목록을 조회합니다. "
                    + "query 파라미터로 가게명을 검색할 수 있으며, "
                    + "역할이 'CUSTOMER' 또는 'MASTER' 또는 'MANAGER' 권한을 가진 사용자만 접근 가능합니다. "
                    + "관리자('MANAGER', 'MASTER')인 사용자는 숨긴처리된 북마크를 포함한 모든 북마크를 조회합니다. "
                    + "일반('CUSTOMER') 사용자는 숨긴처리가 되지 않은 본인의 북마크를 조회합니다."
    )
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
