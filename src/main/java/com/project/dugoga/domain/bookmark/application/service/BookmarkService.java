package com.project.dugoga.domain.bookmark.application.service;

import com.project.dugoga.domain.bookmark.application.dto.BookmarkCreateResponseDto;
import com.project.dugoga.domain.bookmark.application.dto.BookmarkVisibilityUpdateRequestDto;
import com.project.dugoga.domain.bookmark.application.dto.BookmarkListResponseDto;
import com.project.dugoga.domain.bookmark.application.dto.BookmarkUpdateResponseDto;
import com.project.dugoga.domain.bookmark.domain.model.entity.Bookmark;
import com.project.dugoga.domain.bookmark.domain.repository.BookmarkRepository;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.store.domain.repository.StoreRepository;
import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.domain.user.domain.model.enums.UserRoleEnum;
import com.project.dugoga.domain.user.domain.repository.UserRepository;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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

        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Store store = storeRepository.findByIdAndDeletedAtIsNull(storeId)
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

        Store store = storeRepository.findByIdAndDeletedAtIsNull(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        Bookmark bookmark = bookmarkRepository.findByStoreAndUserAndDeletedAtIsNull(store, user)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOKMARK_NOT_FOUND));

        bookmark.delete(userId);
    }

    @Transactional
    public BookmarkUpdateResponseDto visibilityUpdate(@Valid BookmarkVisibilityUpdateRequestDto request, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Store store = storeRepository.findByIdAndDeletedAtIsNull(request.getStoreId())
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        Bookmark bookmark = bookmarkRepository.findByStoreAndUserAndDeletedAtIsNull(store, user)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOKMARK_ALREADY_EXISTS));

        bookmark.updateVisibility(request.getIsHidden());
        return BookmarkUpdateResponseDto.from(bookmark);
    }

    public BookmarkListResponseDto search(Long userId, UserRoleEnum userRole, String query, Pageable pageable) {
        Pageable normalizePageable = normalizePageable(pageable);
        String keyword = (query == null || query.isBlank()) ? null : query.trim();
        return BookmarkListResponseDto.of(bookmarkRepository.search(keyword, userId, isAdmin(userRole), normalizePageable));
    }

    private boolean isAdmin(UserRoleEnum userRole) {
        return userRole.equals(UserRoleEnum.MANAGER) ||
                userRole.equals(UserRoleEnum.MASTER);
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
