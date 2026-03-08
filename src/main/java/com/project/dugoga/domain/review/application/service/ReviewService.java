package com.project.dugoga.domain.review.application.service;

import com.project.dugoga.domain.aiprompt.application.service.AiPromptService;
import com.project.dugoga.domain.order.domain.model.entity.Order;
import com.project.dugoga.domain.order.domain.repository.OrderRepository;
import com.project.dugoga.domain.product.domain.model.entity.Product;
import com.project.dugoga.domain.review.application.dto.ReviewCreateRequestDto;
import com.project.dugoga.domain.review.application.dto.ReviewCreateResponseDto;
import com.project.dugoga.domain.review.domain.model.entity.Review;
import com.project.dugoga.domain.review.domain.repository.ReviewRepository;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.store.domain.repository.StoreRepository;
import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.domain.user.domain.repository.UserRepository;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;
    private final AiPromptService aipromptService;
    private final ChatModel chatModel;

    @Transactional
    public ReviewCreateResponseDto createReview(ReviewCreateRequestDto requestDto, Long userId) {

        UUID storeId = requestDto.getStoreId();
        UUID orderId = requestDto.getOrderId();
        Integer rating = requestDto.getRating();
        String content = requestDto.getContent();
        String imageUrl = requestDto.getImageUrl();
        String gptFilter = aipromptService.getReviewAiPromptText(content);

        if (reviewRepository.existsByOrder_Id(orderId)) {
            throw new BusinessException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        if (gptFilter.contains("실패: ")) {
            throw new BusinessException(ErrorCode.INAPPROPRIATE_REVIEW, gptFilter);
        }

        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Store store = storeRepository.findByIdAndDeletedAtIsNull(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));
        Order order = orderRepository.findByIdAndUser_IdAndDeletedAtIsNull(orderId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        Review review = Review.builder()
                .storeId(store)
                .userId(user)
                .orderId(order)
                .rating(rating)
                .content(content)
                .imageUrl(imageUrl)
                .isHidden(false)
                .build();

        Review saved = reviewRepository.save(review);

        return ReviewCreateResponseDto.from(saved);
    }



}
