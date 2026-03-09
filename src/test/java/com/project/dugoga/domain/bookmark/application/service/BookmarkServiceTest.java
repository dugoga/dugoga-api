package com.project.dugoga.domain.bookmark.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.project.dugoga.domain.availableaddress.domain.model.entity.AvailableAddress;
import com.project.dugoga.domain.bookmark.application.dto.BookmarkCreateResponseDto;
import com.project.dugoga.domain.bookmark.application.dto.BookmarkListResponseDto;
import com.project.dugoga.domain.bookmark.application.dto.BookmarkUpdateResponseDto;
import com.project.dugoga.domain.bookmark.application.dto.BookmarkVisibilityUpdateRequestDto;
import com.project.dugoga.domain.bookmark.domain.model.entity.Bookmark;
import com.project.dugoga.domain.bookmark.domain.repository.BookmarkRepository;
import com.project.dugoga.domain.category.domain.model.entity.Category;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.store.domain.repository.StoreRepository;
import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.domain.user.domain.model.enums.UserRoleEnum;
import com.project.dugoga.domain.user.domain.repository.UserRepository;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;


@ExtendWith(MockitoExtension.class)
class BookmarkServiceTest {
    @Mock
    private BookmarkRepository bookmarkRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private BookmarkService bookmarkService;

    @Nested
    @DisplayName("즐겨찾기 생성")
    class CreateBookmarkTest {
        @Test
        @DisplayName("성공 - 즐겨찾기 생성")
        void createBookmark_success() {

            // given
            Long userId = 1L;
            UUID storeId = UUID.randomUUID();

            User user = User.of(
                    "이메일",
                    "비밀번호",
                    "이름",
                    "닉네임",
                    UserRoleEnum.CUSTOMER);

            Category category = Category.of("KOR", "한식");
            AvailableAddress availableAddress = AvailableAddress.of("서울시", "강남구");

            Store store = Store.of(user, category, availableAddress, "피자 맛집", null,
                    "서울시 강남구 강남대로", "서울시", "강남구",
                    "테헤란로", "2층", 12.2, 12.2,
                    LocalTime.of(8, 30), LocalTime.of(22, 30));

            Bookmark bookmark = Bookmark.of(user, store);

            given(userRepository.findByIdAndDeletedAtIsNull(userId)).willReturn(Optional.of(user));
            given(storeRepository.findByIdAndDeletedAtIsNull(storeId)).willReturn(Optional.of(store));
            given(bookmarkRepository.existsByUser_IdAndStore_Id(userId, storeId)).willReturn(false);
            given(bookmarkRepository.save(any(Bookmark.class))).willReturn(bookmark);

            // when
            BookmarkCreateResponseDto response = bookmarkService.createBookmark(storeId, userId);

            // then
            assertThat(response).isNotNull();

            then(userRepository).should().findByIdAndDeletedAtIsNull(userId);
            then(storeRepository).should().findByIdAndDeletedAtIsNull(storeId);
            then(bookmarkRepository).should().existsByUser_IdAndStore_Id(userId, storeId);
            then(bookmarkRepository).should().save(any(Bookmark.class));
        }

        @Test
        @DisplayName("실패 - 존재하지 않은 회원이면 예외 발생")
        void createBookmark_user_not_found() {

            // given
            Long userId = 1L;
            UUID storeId = UUID.randomUUID();

            given(userRepository.findByIdAndDeletedAtIsNull(userId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> bookmarkService.createBookmark(storeId, userId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.USER_NOT_FOUND.getDefaultMessage());

        }

        @Test
        @DisplayName("실패 - 존재하지 않은 가게면 예외 발생")
        void createBookmark_store_not_found() {

            // given
            Long userId = 1L;
            UUID storeId = UUID.randomUUID();

            User user = User.of(
                    "이메일",
                    "비밀번호",
                    "이름",
                    "닉네임",
                    UserRoleEnum.CUSTOMER);

            given(userRepository.findByIdAndDeletedAtIsNull(userId))
                    .willReturn(Optional.of(user));
            given(storeRepository.findByIdAndDeletedAtIsNull(storeId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> bookmarkService.createBookmark(storeId, userId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.STORE_NOT_FOUND.getDefaultMessage());

        }

        @Test
        @DisplayName("실패 - 이미 존재하는 즐겨찾기 예외 발생")
        void createBookmark_already_exists() {
            // given
            Long userId = 1L;
            UUID storeId = UUID.randomUUID();

            User user = User.of(
                    "이메일",
                    "비밀번호",
                    "이름",
                    "닉네임",
                    UserRoleEnum.CUSTOMER);

            Category category = Category.of("KOR", "한식");
            AvailableAddress availableAddress = AvailableAddress.of("서울시", "강남구");

            Store store = Store.of(user, category, availableAddress, "피자 맛집", null,
                    "서울시 강남구 강남대로", "서울시", "강남구",
                    "테헤란로", "2층", 12.2, 12.2,
                    LocalTime.of(8, 30), LocalTime.of(22, 30));

            given(userRepository.findByIdAndDeletedAtIsNull(userId))
                    .willReturn(Optional.of(user));
            given(storeRepository.findByIdAndDeletedAtIsNull(storeId))
                    .willReturn(Optional.of(store));
            given(bookmarkRepository.existsByUser_IdAndStore_Id(userId, storeId)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> bookmarkService.createBookmark(storeId, userId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.BOOKMARK_ALREADY_EXISTS.getDefaultMessage());
        }

        @Nested
        @DisplayName("즐겨찾기 삭제")
        class DeleteBookmarkTests {

            @Test
            @DisplayName("성공 - 즐겨찾기 삭제")
            void deleteBookmark_success() {

                // given
                Long userId = 1L;
                UUID storeId = UUID.randomUUID();

                User user = User.of(
                        "이메일",
                        "비밀번호",
                        "이름",
                        "닉네임",
                        UserRoleEnum.CUSTOMER);

                Category category = Category.of("KOR", "한식");
                AvailableAddress availableAddress = AvailableAddress.of("서울시", "강남구");

                Store store = Store.of(user, category, availableAddress, "피자 맛집", null,
                        "서울시 강남구 강남대로", "서울시", "강남구",
                        "테헤란로", "2층", 12.2, 12.2,
                        LocalTime.of(8, 30), LocalTime.of(22, 30));

                Bookmark bookmark = Bookmark.of(user, store);

                given(userRepository.findById(userId)).willReturn(Optional.of(user));
                given(storeRepository.findByIdAndDeletedAtIsNull(storeId)).willReturn(Optional.of(store));
                given(bookmarkRepository.findByStoreAndUserAndDeletedAtIsNull(store, user)).willReturn(
                        Optional.of(bookmark));

                // when
                bookmarkService.deleteBookmark(storeId, userId);

                // then
                assertThat(bookmark.getDeletedBy()).isEqualTo(userId);
                assertThat(bookmark.getDeletedAt()).isNotNull();

                then(bookmarkRepository).should().findByStoreAndUserAndDeletedAtIsNull(store, user);
            }

            @Test
            @DisplayName("실패 - 존재하지 않는 유저면 예외 발생")
            void deleteBookmark_user_not_found() {
                // given
                Long userId = 1L;
                UUID storeId = UUID.randomUUID();

                given(userRepository.findById(userId))
                        .willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> bookmarkService.deleteBookmark(storeId, userId))
                        .isInstanceOf(BusinessException.class)
                        .hasMessage(ErrorCode.USER_NOT_FOUND.getDefaultMessage());
            }

            @Test
            @DisplayName("실패 - 존재하지 않는 가게면 예외 발생")
            void deleteBookmark_store_not_found() {
                // given
                Long userId = 1L;
                UUID storeId = UUID.randomUUID();

                User user = User.of(
                        "이메일",
                        "비밀번호",
                        "이름",
                        "닉네임",
                        UserRoleEnum.CUSTOMER);

                given(userRepository.findById(userId)).willReturn(Optional.of(user));

                // when & then
                assertThatThrownBy(() -> bookmarkService.deleteBookmark(storeId, userId))
                        .isInstanceOf(BusinessException.class)
                        .hasMessage(ErrorCode.STORE_NOT_FOUND.getDefaultMessage());

                then(userRepository).should().findById(userId);
                then(storeRepository).should().findByIdAndDeletedAtIsNull(storeId);
            }

            @Test
            @DisplayName("실패 - 존재하지 않는 북마크면 예외 발생")
            void deleteBookmark_bookmark_not_found() {
                // given
                Long userId = 1L;
                UUID storeId = UUID.randomUUID();

                User user = User.of(
                        "이메일",
                        "비밀번호",
                        "이름",
                        "닉네임",
                        UserRoleEnum.CUSTOMER);

                Category category = Category.of("KOR", "한식");
                AvailableAddress availableAddress = AvailableAddress.of("서울시", "강남구");

                Store store = Store.of(user, category, availableAddress, "피자 맛집", null,
                        "서울시 강남구 강남대로", "서울시", "강남구",
                        "테헤란로", "2층", 12.2, 12.2,
                        LocalTime.of(8, 30), LocalTime.of(22, 30));

                given(userRepository.findById(userId)).willReturn(Optional.of(user));
                given(storeRepository.findByIdAndDeletedAtIsNull(storeId)).willReturn(Optional.of(store));
                given(bookmarkRepository.findByStoreAndUserAndDeletedAtIsNull(store, user)).willReturn(
                        Optional.empty());

                // when & then
                assertThatThrownBy(() -> bookmarkService.deleteBookmark(storeId, userId))
                        .isInstanceOf(BusinessException.class)
                        .hasMessage(ErrorCode.BOOKMARK_NOT_FOUND.getDefaultMessage());

                then(userRepository).should().findById(userId);
                then(storeRepository).should().findByIdAndDeletedAtIsNull(storeId);
                then(bookmarkRepository).should().findByStoreAndUserAndDeletedAtIsNull(store, user);
            }
        }

        @Nested
        @DisplayName("즐겨찾기 숨김 여부 수정")
        class VisibilityUpdateTests {

            @Test
            @DisplayName("성공 - 즐겨찾기 숨김 여부 수정")
            void visibilityUpdate_success() {
                // given
                Long userId = 1L;
                UUID storeId = UUID.randomUUID();

                BookmarkVisibilityUpdateRequestDto request =
                        new BookmarkVisibilityUpdateRequestDto(storeId, true);

                User user = User.of(
                        "이메일",
                        "비밀번호",
                        "이름",
                        "닉네임",
                        UserRoleEnum.CUSTOMER
                );

                Category category = Category.of("KOR", "한식");
                AvailableAddress availableAddress = AvailableAddress.of("서울시", "강남구");

                Store store = Store.of(
                        user, category, availableAddress, "피자 맛집", null,
                        "서울시 강남구 강남대로", "서울시", "강남구",
                        "테헤란로", "2층", 12.2, 12.2,
                        LocalTime.of(8, 30), LocalTime.of(22, 30)
                );

                Bookmark bookmark = Bookmark.of(user, store);

                given(userRepository.findById(userId)).willReturn(Optional.of(user));
                given(storeRepository.findByIdAndDeletedAtIsNull(storeId)).willReturn(Optional.of(store));
                given(bookmarkRepository.findByStoreAndUserAndDeletedAtIsNull(store, user))
                        .willReturn(Optional.of(bookmark));

                // when
                BookmarkUpdateResponseDto response = bookmarkService.visibilityUpdate(request, userId);

                // then
                assertThat(response).isNotNull();
                assertThat(bookmark.isHidden()).isTrue();

                then(userRepository).should().findById(userId);
                then(storeRepository).should().findByIdAndDeletedAtIsNull(storeId);
                then(bookmarkRepository).should().findByStoreAndUserAndDeletedAtIsNull(store, user);
            }

            @Test
            @DisplayName("실패 - 존재하지 않는 회원이면 예외 발생")
            void visibilityUpdate_user_not_found() {
                // given
                Long userId = 1L;
                UUID storeId = UUID.randomUUID();

                BookmarkVisibilityUpdateRequestDto request =
                        new BookmarkVisibilityUpdateRequestDto(storeId, true);

                given(userRepository.findById(userId)).willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> bookmarkService.visibilityUpdate(request, userId))
                        .isInstanceOf(BusinessException.class)
                        .hasMessage(ErrorCode.USER_NOT_FOUND.getDefaultMessage());

                then(userRepository).should().findById(userId);
            }

            @Test
            @DisplayName("실패 - 존재하지 않는 가게면 예외 발생")
            void visibilityUpdate_store_not_found() {
                // given
                Long userId = 1L;
                UUID storeId = UUID.randomUUID();

                BookmarkVisibilityUpdateRequestDto request =
                        new BookmarkVisibilityUpdateRequestDto(storeId, true);

                User user = User.of(
                        "이메일",
                        "비밀번호",
                        "이름",
                        "닉네임",
                        UserRoleEnum.CUSTOMER
                );

                given(userRepository.findById(userId)).willReturn(Optional.of(user));
                given(storeRepository.findByIdAndDeletedAtIsNull(storeId)).willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> bookmarkService.visibilityUpdate(request, userId))
                        .isInstanceOf(BusinessException.class)
                        .hasMessage(ErrorCode.STORE_NOT_FOUND.getDefaultMessage());

                then(userRepository).should().findById(userId);
                then(storeRepository).should().findByIdAndDeletedAtIsNull(storeId);
            }

            @Test
            @DisplayName("실패 - 존재하지 않는 즐겨찾기면 예외 발생")
            void visibilityUpdate_bookmark_not_found() {
                // given
                Long userId = 1L;
                UUID storeId = UUID.randomUUID();

                BookmarkVisibilityUpdateRequestDto request =
                        new BookmarkVisibilityUpdateRequestDto(storeId, true);

                User user = User.of(
                        "이메일",
                        "비밀번호",
                        "이름",
                        "닉네임",
                        UserRoleEnum.CUSTOMER
                );

                Category category = Category.of("KOR", "한식");
                AvailableAddress availableAddress = AvailableAddress.of("서울시", "강남구");

                Store store = Store.of(
                        user, category, availableAddress, "피자 맛집", null,
                        "서울시 강남구 강남대로", "서울시", "강남구",
                        "테헤란로", "2층", 12.2, 12.2,
                        LocalTime.of(8, 30), LocalTime.of(22, 30)
                );

                given(userRepository.findById(userId)).willReturn(Optional.of(user));
                given(storeRepository.findByIdAndDeletedAtIsNull(storeId)).willReturn(Optional.of(store));
                given(bookmarkRepository.findByStoreAndUserAndDeletedAtIsNull(store, user))
                        .willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> bookmarkService.visibilityUpdate(request, userId))
                        .isInstanceOf(BusinessException.class)
                        .hasMessage(ErrorCode.BOOKMARK_NOT_FOUND.getDefaultMessage());

                then(userRepository).should().findById(userId);
                then(storeRepository).should().findByIdAndDeletedAtIsNull(storeId);
                then(bookmarkRepository).should().findByStoreAndUserAndDeletedAtIsNull(store, user);
            }

            @Test
            @DisplayName("실패 - 숨김 여부가 동일하면 예외 발생")
            void visibilityUpdate_unchanged() {
                // given
                Long userId = 1L;
                UUID storeId = UUID.randomUUID();

                BookmarkVisibilityUpdateRequestDto request =
                        new BookmarkVisibilityUpdateRequestDto(storeId, false);

                User user = User.of(
                        "이메일",
                        "비밀번호",
                        "이름",
                        "닉네임",
                        UserRoleEnum.CUSTOMER
                );

                Category category = Category.of("KOR", "한식");
                AvailableAddress availableAddress = AvailableAddress.of("서울시", "강남구");

                Store store = Store.of(
                        user, category, availableAddress, "피자 맛집", null,
                        "서울시 강남구 강남대로", "서울시", "강남구",
                        "테헤란로", "2층", 12.2, 12.2,
                        LocalTime.of(8, 30), LocalTime.of(22, 30)
                );

                Bookmark bookmark = Bookmark.of(user, store);

                given(userRepository.findById(userId)).willReturn(Optional.of(user));
                given(storeRepository.findByIdAndDeletedAtIsNull(storeId)).willReturn(Optional.of(store));
                given(bookmarkRepository.findByStoreAndUserAndDeletedAtIsNull(store, user))
                        .willReturn(Optional.of(bookmark));

                // when & then
                assertThatThrownBy(() -> bookmarkService.visibilityUpdate(request, userId))
                        .isInstanceOf(BusinessException.class)
                        .hasMessage(ErrorCode.BOOKMARK_VISIBILITY_UNCHANGED.getDefaultMessage());

                then(userRepository).should().findById(userId);
                then(storeRepository).should().findByIdAndDeletedAtIsNull(storeId);
                then(bookmarkRepository).should().findByStoreAndUserAndDeletedAtIsNull(store, user);
            }
        }

        @Nested
        @DisplayName("즐겨찾기 조회")
        class SearchBookmark {
            @Test
            @DisplayName("성공 - 즐겨찾기 목록 조회")
            void search_success() {

                // given
                Long userId = 1L;
                User user = User.of(
                        "이메일",
                        "비밀번호",
                        "이름",
                        "닉네임",
                        UserRoleEnum.CUSTOMER
                );

                Category category = Category.of("KOR", "한식");
                AvailableAddress availableAddress = AvailableAddress.of("서울시", "강남구");

                Store store = Store.of(
                        user, category, availableAddress, "피자 맛집", null,
                        "서울시 강남구 강남대로", "서울시", "강남구",
                        "테헤란로", "2층", 12.2, 12.2,
                        LocalTime.of(8, 30), LocalTime.of(22, 30)
                );

                Bookmark bookmark = Bookmark.of(user, store);

                String query = "강남";
                Pageable pageable = PageRequest.of(0, 10, Sort.by(Direction.DESC, "createdAt"));
                UserRoleEnum role = UserRoleEnum.CUSTOMER;

                List<Bookmark> bookmarks = List.of(bookmark);
                Page<Bookmark> page = new PageImpl<>(bookmarks, pageable, 1);

                given(bookmarkRepository.search("강남", userId, false, pageable))
                        .willReturn(page);

                // when
                BookmarkListResponseDto response = bookmarkService.search(userId, role, query, pageable);

                // then
                assertThat(response).isNotNull();
                then(bookmarkRepository).should().search("강남", userId, false, pageable);
            }

        }

    }

}