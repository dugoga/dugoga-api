package com.project.dugoga.domain.bookmark.application.service;

import com.project.dugoga.domain.bookmark.application.dto.BookmarkCreateResponseDto;
import com.project.dugoga.domain.bookmark.application.dto.UserBookmarkListResponseDto;
import com.project.dugoga.domain.bookmark.application.dto.BookmarkUpdateResponseDto;
import com.project.dugoga.domain.bookmark.application.dto.AdminBookmarkListResponseDto;
import com.project.dugoga.domain.bookmark.domain.model.entity.Bookmark;
import com.project.dugoga.domain.bookmark.domain.repository.BookmarkRepository;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.store.domain.repository.StoreRepository;
import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.domain.user.domain.repository.UserRepository;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final BookmarkRepository bookmarkRepository;

    @Transactional
    public BookmarkCreateResponseDto createBookmark(UUID storeId, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        if(bookmarkRepository.existsByUser_IdAndStore_Id(userId, storeId)) {
            throw new BusinessException(ErrorCode.BOOKMARK_ALREADY_EXISTS);
        }

        Bookmark bookmark = Bookmark.of(user, store);
        Bookmark saved = bookmarkRepository.save(bookmark);

        return BookmarkCreateResponseDto.from(saved);

    }

    @Transactional
    public void deleteBookmark(UUID storeId, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        Bookmark bookmark = bookmarkRepository.findByStoreAndUser(store, user)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOKMARK_NOT_FOUND));

        if(bookmark.isDeleted()) {
            throw new BusinessException(ErrorCode.BOOKMARK_ALREADY_DELETED);
        }
        bookmark.delete(userId);
    }

    @Transactional
    public BookmarkUpdateResponseDto restoreBookmark(UUID storeId, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        Bookmark bookmark = bookmarkRepository.findByStoreAndUser(store, user)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOKMARK_NOT_FOUND));

        if(!bookmark.isDeleted()) {
            throw new BusinessException(ErrorCode.BOOKMARK_NOT_DELETED);
        }
        bookmark.restore();

        return BookmarkUpdateResponseDto.from(bookmark);
    }

    public UserBookmarkListResponseDto searchUserBookmarkList(Long userId, String query, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Pageable normalized = normalizePageable(pageable);
        String keyword = (query == null || query.isBlank()) ? null : query.trim();
        Page<Bookmark> bookmarkPage = findBookmarkUser(user, keyword, normalized);

        return UserBookmarkListResponseDto.of(bookmarkPage);
    }

    public AdminBookmarkListResponseDto searchAdminBookmarkList(Long userId, String query, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Pageable normalized = normalizePageable(pageable);
        String keyword = (query == null || query.isBlank()) ? null : query.trim();
        Page<Bookmark> bookmarkPage = findBookAdmin(user, keyword, normalized);

        return AdminBookmarkListResponseDto.of(bookmarkPage);
    }

    private Page<Bookmark> findBookAdmin(User user, String keyword, Pageable pageable) {
        return (keyword == null)
                ? bookmarkRepository.findByUser(user, pageable)
                : bookmarkRepository.findByUserAndStore_NameContaining(user, keyword, pageable);
    }


    private Page<Bookmark> findBookmarkUser(User user, String keyword, Pageable normalized) {
        return (keyword == null)
                ? bookmarkRepository.findByUserAndDeletedAtIsNull(user, normalized)
                : bookmarkRepository.findByUserAndStore_NameContainingAndDeletedAtIsNull(user, keyword, normalized);
    }

    private Pageable normalizePageable(Pageable pageable) {

        int page = Math.max(pageable.getPageNumber(), 0);

        int requestedSize = pageable.getPageSize();
        int size = (requestedSize == 10 || requestedSize == 30 || requestedSize == 50)
                ? requestedSize
                : 10;

        Sort sort = pageable.getSort().isSorted()
                ? pageable.getSort()
                : Sort.by(Sort.Direction.DESC, "createdAt");

        return PageRequest.of(page, size, sort);
    }
}
