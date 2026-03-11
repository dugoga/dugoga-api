package com.project.dugoga.domain.aiprompt.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.project.dugoga.domain.aiprompt.application.dto.*;
import com.project.dugoga.domain.aiprompt.application.service.AiPromptService;
import com.project.dugoga.domain.aiprompt.domain.model.entity.AiPrompt;
import com.project.dugoga.domain.aiprompt.domain.repository.AiPromptRepository;
import com.project.dugoga.domain.availableaddress.domain.model.entity.AvailableAddress;
import com.project.dugoga.domain.category.domain.model.entity.Category;
import com.project.dugoga.domain.product.domain.model.entity.Product;
import com.project.dugoga.domain.product.domain.repository.ProductRepository;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.store.domain.repository.StoreRepository;
import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.domain.user.domain.model.enums.UserRoleEnum;
import com.project.dugoga.domain.user.domain.repository.UserRepository;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
import com.project.dugoga.global.security.jwt.CustomUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class AiPromptServiceTest {

    @Mock
    private AiPromptRepository aiPromptRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ChatModel chatModel;

    @InjectMocks
    private AiPromptService aiPromptService;

    @Nested
    @DisplayName("AI 프롬프트 상품 설명 생성")
    class CreateAiPrompt {

        @Test
        @DisplayName("성공 - AI 상품 설명 생성")
        void createAiPrompt_success() {
            // given
            Long userId = 7L;
            UUID storeId = UUID.randomUUID();
            UUID productId = UUID.randomUUID();
            UUID aiPromptId = UUID.randomUUID();
            String promptText = "치즈향이 풍부하며 짜지도 달지도 않는 맛있는 피자라는 점을 부각해서 작성해줘.";
            String expectedAiResponse = "치즈향이 가득하고 자극적이지 않아 더욱 맛있는 최강피자입니다.";

            AiPromptCreateRequestDto requestDto = new AiPromptCreateRequestDto(userId, storeId, productId, promptText);

            User user = User.of(
                    "email@test.com",
                    "passWord1@",
                    "hong",
                    "xiest",
                    UserRoleEnum.CUSTOMER);
            ReflectionTestUtils.setField(user, "id", userId);

            Category category = Category.of("PIZ", "피자 전문점");
            AvailableAddress availableAddress = AvailableAddress.of("서울시", "강남구");
            Store store = Store.of(user, category, availableAddress, "피자 맛집", null,
                    "서울시 강남구 강남대로", "서울시", "강남구", "테헤란로", "2층", 12.2, 12.2,
                    LocalTime.of(8, 30), LocalTime.of(22, 30));
            ReflectionTestUtils.setField(store, "id", storeId);

            Product product = Product.create(store, "최강피자", "", 15000, "../../../");
            ReflectionTestUtils.setField(product, "id", productId);

            AiPrompt savedAiPrompt = AiPrompt.builder()
                    .user(user).store(store).product(product)
                    .promptText(promptText).responseText(expectedAiResponse).build();
            ReflectionTestUtils.setField(savedAiPrompt, "id", aiPromptId);

            ChatResponse mockChatResponse = new ChatResponse(List.of(new Generation(new AssistantMessage(expectedAiResponse))));

            given(userRepository.findByIdAndDeletedAtIsNull(userId)).willReturn(Optional.of(user));
            given(storeRepository.findByIdAndDeletedAtIsNull(storeId)).willReturn(Optional.of(store));
            given(productRepository.findByIdAndDeletedAtIsNull(productId)).willReturn(Optional.of(product));
            given(chatModel.call(any(Prompt.class))).willReturn(mockChatResponse);
            given(aiPromptRepository.save(any(AiPrompt.class))).willReturn(savedAiPrompt);

            // when
            AiPromptCreateResponseDto responseDto = aiPromptService.createAiPrompt(requestDto, userId);

            // then
            assertThat(responseDto).isNotNull();
            assertThat(responseDto.getId()).isEqualTo(aiPromptId);

            then(userRepository).should().findByIdAndDeletedAtIsNull(userId);
            then(storeRepository).should().findByIdAndDeletedAtIsNull(storeId);
            then(productRepository).should().findByIdAndDeletedAtIsNull(productId);
            then(chatModel).should().call(any(Prompt.class));
            then(aiPromptRepository).should().save(any(AiPrompt.class));
        }

        @Test
        @DisplayName("실패 - 삭제되거나 존재하지 않는 계정으로 요청")
        void createAiPrompt_fail_user_not_found() {
            // given
            Long userId = 777L;
            UUID storeId = UUID.randomUUID();
            UUID productId = UUID.randomUUID();
            String promptText = "치즈향 좋은 피자를 강조해서 설명해줘.";

            AiPromptCreateRequestDto requestDto = new AiPromptCreateRequestDto(userId, storeId, productId, promptText);

            given(userRepository.findByIdAndDeletedAtIsNull(userId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> aiPromptService.createAiPrompt(requestDto, userId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.USER_NOT_FOUND.getDefaultMessage());

            then(storeRepository).shouldHaveNoInteractions();
            then(productRepository).shouldHaveNoInteractions();
            then(chatModel).shouldHaveNoInteractions();
            then(aiPromptRepository).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("AI 프롬프트 상품 설명 재생성")
    class RecreateAiPrompt {

        @Test
        @DisplayName("성공 - AI 상품 설명 재생성")
        void recreateAiPrompt_success() {
            // given
            Long userId = 7L;
            UUID storeId = UUID.randomUUID();
            UUID productId = UUID.randomUUID();
            UUID promptId = UUID.randomUUID();
            String newPromptText = "치즈크러스트가 두꺼운 피자를 강조해서 설명해줘";
            String expectedAiResponse = "치즈크러스트 두께만 7cm인 피자입니다.";

            User user = User.of(
                    "email@test.com",
                    "passWord1@",
                    "hong",
                    "xiest",
                    UserRoleEnum.CUSTOMER);
            ReflectionTestUtils.setField(user, "id", userId);

            Category category = Category.of("PIZ", "피자 전문점");
            AvailableAddress availableAddress = AvailableAddress.of("서울시", "강남구");

            Store store = Store.of(user, category, availableAddress, "피자 맛집", null,
                    "서울시 강남구 강남대로", "서울시", "강남구", "테헤란로", "2층", 12.2, 12.2,
                    LocalTime.of(8, 30), LocalTime.of(22, 30));
            ReflectionTestUtils.setField(store, "id", storeId);
            ReflectionTestUtils.setField(store, "averageRating", 5.0);
            ReflectionTestUtils.setField(store, "reviewCount", 1L);

            Product product = Product.create(store, "최강피자", "", 15000, "../../../");
            ReflectionTestUtils.setField(product, "id", productId);

            AiPrompt existingAiPrompt = AiPrompt.builder()
                    .user(user).store(store).product(product).promptText("이전 프롬프트 텍스트").responseText("이전 상품 설명").build();
            ReflectionTestUtils.setField(existingAiPrompt, "id", promptId);

            AiPromptRecreateRequestDto requestDto = new AiPromptRecreateRequestDto(newPromptText);
            ChatResponse mockChatResponse = new ChatResponse(List.of(new Generation(new AssistantMessage(expectedAiResponse))));

            given(aiPromptRepository.findByIdAndDeletedAtIsNull(promptId)).willReturn(Optional.of(existingAiPrompt));
            given(chatModel.call(any(Prompt.class))).willReturn(mockChatResponse);

            // when
            AiPromptRecreateResponseDto responseDto = aiPromptService.recreateAiPrompt(requestDto, promptId, userId);

            // then
            assertThat(responseDto).isNotNull();
            assertThat(responseDto.getId()).isEqualTo(promptId);
            assertThat(existingAiPrompt.getPromptText()).isEqualTo(newPromptText);
            assertThat(existingAiPrompt.getResponseText()).isEqualTo(expectedAiResponse);

            then(aiPromptRepository).should().findByIdAndDeletedAtIsNull(promptId);
            then(chatModel).should().call(any(Prompt.class));
        }

        @Test
        @DisplayName("실패 - 기존 생성자와 재요청자가 다른 경우")
        void recreateAiPrompt_fail_not_owner() {
            // given
            Long ownerId = 7L;
            Long requesterId = 99L;
            UUID promptId = UUID.randomUUID();

            User owner = User.of(
                    "email@test.com",
                    "passWord1@",
                    "hong",
                    "xiest",
                    UserRoleEnum.CUSTOMER);
            ReflectionTestUtils.setField(owner, "id", ownerId);

            AiPrompt existingAiPrompt = AiPrompt.builder()
                    .user(owner).promptText("이전 프롬프트 텍스트").responseText("이전 상품 설명").build();
            ReflectionTestUtils.setField(existingAiPrompt, "id", promptId);

            AiPromptRecreateRequestDto requestDto = new AiPromptRecreateRequestDto("새로운 요청");

            given(aiPromptRepository.findByIdAndDeletedAtIsNull(promptId)).willReturn(Optional.of(existingAiPrompt));

            // when & then
            assertThatThrownBy(() -> aiPromptService.recreateAiPrompt(requestDto, promptId, requesterId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.AI_PROMPT_NOT_OWNER.getDefaultMessage());

            then(chatModel).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("AI 프롬프트 상품 설명 조회")
    class GetAiPrompt {

        @Test
        @DisplayName("성공 - AI 상품 설명 상세 조회")
        void getDetailAiPrompt_success() {
            // given
            UUID promptId = UUID.randomUUID();
            Long userId = 7L;

            User user = User.of(
                    "email@test.com",
                    "passWord1@",
                    "hong",
                    "xiest",
                    UserRoleEnum.CUSTOMER);
            ReflectionTestUtils.setField(user, "id", userId);

            Category category = Category.of("PIZ", "피자");
            AvailableAddress availableAddress = AvailableAddress.of("서울시", "강남구");
            Store store = Store.of(user, category, availableAddress, "피자 맛집", null,
                    "서울시 강남구 강남대로", "서울시", "강남구", "테헤란로", "2층", 12.2, 12.2,
                    LocalTime.of(8, 30), LocalTime.of(22, 30));
            ReflectionTestUtils.setField(store, "id", UUID.randomUUID());

            Product product = Product.create(store, "최강피자", "", 15000, "../../../");
            ReflectionTestUtils.setField(product, "id", UUID.randomUUID());

            AiPrompt existingAiPrompt = AiPrompt.builder()
                    .user(user).store(store).product(product)
                    .promptText("치즈향 강조").responseText("치즈 풍미가 깊은 피자").build();
            ReflectionTestUtils.setField(existingAiPrompt, "id", promptId);
            ReflectionTestUtils.setField(existingAiPrompt, "createdAt", LocalDateTime.now());

            given(aiPromptRepository.findByIdAndDeletedAtIsNull(promptId)).willReturn(Optional.of(existingAiPrompt));

            // when
            AiPromptGetResponseDto responseDto = aiPromptService.getAiPrompt(promptId);

            // then
            assertThat(responseDto).isNotNull();
            assertThat(responseDto.getId()).isEqualTo(promptId);
            assertThat(responseDto.getUserId()).isEqualTo(userId);
            assertThat(responseDto.getStoreId()).isEqualTo(store.getId());

            then(aiPromptRepository).should().findByIdAndDeletedAtIsNull(promptId);
        }

        @Test
        @DisplayName("실패 - 삭제되거나 존재하지 않는 상품 설명 조회")
        void getDetailAiPrompt_fail_not_found() {
            // given
            UUID promptId = UUID.randomUUID();
            given(aiPromptRepository.findByIdAndDeletedAtIsNull(promptId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> aiPromptService.getAiPrompt(promptId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.AI_PROMPT_NOT_FOUND.getDefaultMessage());
        }
    }

    @Nested
    @DisplayName("AI 프롬프트 상품 설명 삭제")
    class DeleteAiPrompt {

        @Test
        @DisplayName("성공 - AI 상품 설명 삭제")
        void deleteAiPrompt_success() {
            // given
            UUID promptId = UUID.randomUUID();
            Long userId = 7L;

            User user = User.of(
                    "email@test.com",
                    "passWord1@",
                    "hong",
                    "xiest",
                    UserRoleEnum.CUSTOMER);
            ReflectionTestUtils.setField(user, "id", userId);

            AiPrompt existingAiPrompt = AiPrompt.builder().user(user).build();
            ReflectionTestUtils.setField(existingAiPrompt, "id", promptId);

            CustomUserDetails userDetails = mock(CustomUserDetails.class);
            given(userDetails.getId()).willReturn(userId);
            given(aiPromptRepository.findByIdAndDeletedAtIsNull(promptId)).willReturn(Optional.of(existingAiPrompt));

            // when
            aiPromptService.deleteAiPrompt(promptId, userDetails);

            // then
            assertThat(existingAiPrompt.getDeletedAt()).isNotNull();
            assertThat(existingAiPrompt.getDeletedBy()).isEqualTo(userId);

            then(aiPromptRepository).should().findByIdAndDeletedAtIsNull(promptId);
        }

        @Test
        @DisplayName("실패 - 삭제할 상품 설명이 없는 경우")
        void deleteAiPrompt_fail_not_found() {
            // given
            UUID promptId = UUID.randomUUID();
            CustomUserDetails userDetails = mock(CustomUserDetails.class);

            given(aiPromptRepository.findByIdAndDeletedAtIsNull(promptId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> aiPromptService.deleteAiPrompt(promptId, userDetails))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.AI_PROMPT_NOT_FOUND.getDefaultMessage());
        }
    }
}
