package com.project.dugoga.domain.review.application.service;

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
    private final ChatModel chatModel;

    @Transactional
    public ReviewCreateResponseDto createReview(ReviewCreateRequestDto requestDto, Long userId) {

        UUID storeId = requestDto.getStoreId();
        UUID orderId = requestDto.getOrderId();
        Integer rating = requestDto.getRating();
        String content = requestDto.getContent();
        String imageUrl = requestDto.getImageUrl();
        String gptFilter = getAiPromptText(content);

        if (gptFilter.contains("실패")) {
            throw new BusinessException(ErrorCode.INAPPROPRIATE_REVIEW, gptFilter);
        }

        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Store store = storeRepository.findByIdAndDeletedAtIsNull(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));
        Order order = orderRepository.findById(orderId)
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

    public String getAiPromptText(String content) {

        String systemInstruction = """
            당신은 부적절한 리뷰를 검수하는 사람입니다.
            
            사용자가 작성한 리뷰의 내용에 비속어(욕설)이 들어있는 경우,
            "실패: 이유" 형식으로 응답하고,
            
            없는 경우 "성공" 이라고만 응답하세요.
            """;

        String userPromptText = String.format("""   
        [사용자 리뷰 내용]
        %s
        """,
                content
        );

        SystemMessage systemMessage = SystemMessage.builder()
                .text(systemInstruction)
                .build();

        UserMessage userMessage = UserMessage.builder()
                .text(userPromptText)
                .build();

        OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                .model("gpt-5-mini")
                .maxCompletionTokens(5000)
                .temperature(1.0)
                .build();

        Prompt prompt = Prompt.builder()
                .messages(systemMessage, userMessage)
                .chatOptions(chatOptions)
                .build();

        ChatResponse chatResponse = chatModel.call(prompt);
        AssistantMessage assistantMessage = chatResponse.getResult().getOutput();

        return assistantMessage.getText();
    }

}
