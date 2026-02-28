package com.project.dugoga.domain.store.application.service;

import com.project.dugoga.domain.category.domain.model.entity.Category;
import com.project.dugoga.domain.category.domain.repository.categoryRepository;
import com.project.dugoga.domain.store.application.dto.StoreCreateRequestDto;
import com.project.dugoga.domain.store.application.dto.StoreCreateResponseDto;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.store.domain.repository.StoreRepository;
import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.domain.user.domain.model.enums.UserRoleEnum;
import com.project.dugoga.domain.user.domain.repository.UserRepository;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreService {
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final categoryRepository categoryRepository;

    public StoreCreateResponseDto createStore(StoreCreateRequestDto request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BusinessException(ErrorCode.BAD_REQUEST, "존재하지 않는 사용자입니다.")
        );
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(
                () -> new BusinessException(ErrorCode.BAD_REQUEST, "존재하지 않는 카테고리입니다.")
        );

        if (user.getUserRole().equals(UserRoleEnum.CUSTOMER)) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "일반 사용자는 가게를 등록할 수 없습니다.");
        }

        Store store = Store.of(
                user, category,
                request.getName(), request.getComment(),
                request.getAddressName(), request.getRegion1depthName(), request.getRegion2depthName(), request.getRegion3depthName(),
                request.getDetailAddress(), request.getLongitude(), request.getLatitude(),
                request.getOpenAt(), request.getCloseAt());
        Store saved = storeRepository.save(store);

        return StoreCreateResponseDto.from(saved);
    }
}
