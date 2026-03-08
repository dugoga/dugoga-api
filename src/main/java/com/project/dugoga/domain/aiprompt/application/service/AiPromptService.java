package com.project.dugoga.domain.aiprompt.application.service;

import com.project.dugoga.domain.aiprompt.application.dto.*;
import com.project.dugoga.domain.aiprompt.domain.model.entity.AiPrompt;
import com.project.dugoga.domain.aiprompt.domain.repository.AiPromptRepository;
import com.project.dugoga.domain.product.domain.model.entity.Product;
import com.project.dugoga.domain.product.domain.repository.ProductRepository;
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
public class AiPromptService {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final AiPromptRepository aiPromptRepository;
    private final ChatModel chatModel;

    @Transactional
    // TODO : 로그인 이후 authentication에서 user-id 가져오도록 변경 필요
    public AiPromptCreateResponseDto createAiPrompt(AiPromptCreateRequestDto requestDto) {

        Long userId = requestDto.getUserId();
        UUID storeId = requestDto.getStoreId();
        UUID productId = requestDto.getProductId();
        String promptText = requestDto.getPromptText();

        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Store store = storeRepository.findByIdAndDeletedAtIsNull(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));
        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        AiPrompt aiPrompt = AiPrompt.builder()
                .userId(user)
                .storeId(store)
                .productId(product)
                .promptText(promptText)
                .responseText(getAiPromptText(store, product, promptText))
                .build();

        AiPrompt saved = aiPromptRepository.save(aiPrompt);

        return AiPromptCreateResponseDto.from(saved);
    }

    @Transactional
    // TODO : 로그인 기능 구현 이후 기존 등록자와 재등록 요청자 비교 추가 필요
    public AiPromptRecreateResponseDto recreateAiPrompt(UUID id, AiPromptRecreateRequestDto requestDto) {

        String newPromptText = requestDto.getPromptText();

        AiPrompt aiPrompt = aiPromptRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.AI_PROMPT_NOT_FOUND));
        User user = aiPrompt.getUserId();
        Store store = aiPrompt.getStoreId();
        Product product = aiPrompt.getProductId();

        aiPrompt.updateAiPrompt(newPromptText, getAiPromptText(store, product, newPromptText));

        return AiPromptRecreateResponseDto.from(aiPrompt);
    }

    @Transactional(readOnly = true)
    public AiPromptGetResponseDto getAiPrompt(UUID id) {

        AiPrompt aiPrompt = aiPromptRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.AI_PROMPT_NOT_FOUND));

        return AiPromptGetResponseDto.from(aiPrompt);
    }

    @Transactional
    public void deleteAiPrompt(UUID id, Long userId) {

        AiPrompt aiPrompt = aiPromptRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.AI_PROMPT_NOT_FOUND));

        aiPrompt.delete(userId);
    }

    @Transactional
    public AiPromptRestoreResponseDto restoreAiPrompt(UUID id) {

        AiPrompt aiPrompt = aiPromptRepository.findByIdAndDeletedAtIsNotNull(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.AI_PROMPT_NOT_FOUND));

        aiPrompt.restore();

        return AiPromptRestoreResponseDto.from(aiPrompt);
    }

    public String getAiPromptText(Store store, Product product, String promptText) {

        String systemInstruction = """
            당신은 음식점의 전문 마케팅 카피라이터입니다.
            사용자가 제공하는 '가게 정보', '상품 정보', '요청 사항'을 바탕으로
            고객들이 해당 상품(음식)을 먹고 싶어하게 상품 설명을 작성해주세요.
            
            사용자 요청이 음식 설명 요청과 관계 없는 경우,
            "음식 설명 요청 외 다른 질문은 응답이 불가합니다." 라고 응답하세요.
            
            - 말투: 정중하고 신뢰감 있으면서 최대한 풍부하게
            - 길이: 500자 이내로
            - 이모지: 사용하지 않음
            """;

        String userPromptText = String.format("""
        [정보]
        - 가게 이름: %s
        - 식당 카테고리: %s
        - 상품 이름: %s
        
        [요청 사항]
        %s
        """,
                store.getName(),
                store.getCategory().getName(),
                product.getName(),
                promptText
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

    public String getReviewAiPromptText(String content) {

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
