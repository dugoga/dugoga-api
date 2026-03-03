package com.project.dugoga.domain.store.application.service;

import com.project.dugoga.domain.availableaddress.domain.repository.AvailableAddressRepository;
import com.project.dugoga.domain.category.domain.model.entity.Category;
import com.project.dugoga.domain.category.domain.repository.CategoryRepository;
import com.project.dugoga.domain.store.application.dto.*;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final AvailableAddressRepository availableAddressRepository;
    private final CategoryRepository categoryRepository;

    // CUSTOMER X
    @Transactional
    public StoreCreateResponseDto createStore(StoreCreateRequestDto request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND)
        );

        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(
                () -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND)
        );

        validateServiceArea(request.getRegion1depthName(), request.getRegion2depthName());

        Store store = Store.of(
                user, category,
                request.getName(), request.getComment(),
                request.getAddressName(), request.getRegion1depthName(), request.getRegion2depthName(), request.getRegion3depthName(),
                request.getDetailAddress(), request.getLongitude(), request.getLatitude(),
                request.getOpenAt(), request.getCloseAt());
        Store saved = storeRepository.save(store);

        return StoreCreateResponseDto.from(saved);
    }

    // CUSTOMER X
    @Transactional
    public StoreUpdateResponseDto updateStore(StoreUpdateRequestDto request, UUID storeId, Long userId, UserRoleEnum userRole) {
        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new BusinessException(ErrorCode.STORE_NOT_FOUND)
        );
        if (isAuthorized(store, userId, userRole)) {
            store.validateOwner(userId);
        }

        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(
                () -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND)
        );

        validateServiceArea(request.getRegion1depthName(), request.getRegion2depthName());

        store.update(category, request.getName(), request.getComment(),
                request.getAddressName(), request.getRegion1depthName(), request.getRegion2depthName(),
                request.getRegion3depthName(), request.getDetailAddress(), request.getLongitude(),
                request.getLatitude(), request.getOpenAt(), request.getCloseAt());

        return StoreUpdateResponseDto.from(store);
    }

    // CUSTOMER X
    public StoreStatusUpdateResponse statusUpdate(StoreStatusUpdateRequest request, Long userId, UserRoleEnum userRole) {
        Set<Store> foundStores = storeRepository.findByIdIn(request.getStoreIds());
        Set<UUID> foundIdsSet = foundStores.stream()
                .map(Store::getId)
                .collect(Collectors.toSet());

        List<UUID> successIds = new ArrayList<>();
        List<UUID> missingIds = request.getStoreIds().stream()
                .filter(id -> !foundIdsSet.contains(id))
                .toList();
        List<UUID> failIds = new ArrayList<>(missingIds);

        for (Store store : foundStores) {
            if (isAuthorized(store, userId, userRole)) {
                store.updateStatus(request.getStatus());
                successIds.add(store.getId());
            } else {
                failIds.add(store.getId());
            }
        }

        return StoreStatusUpdateResponse.of(successIds, failIds, LocalDateTime.now());
    }

    // CUSTOMER X, OWNER X
    @Transactional
    public StoreVisibilityUpdateResponseDto visibilityUpdate(StoreVisibilityUpdateRequestDto request) {
        Set<Store> foundStores = storeRepository.findByIdIn(request.getStoreIds());

        Set<UUID> foundIdsSet = foundStores.stream()
                .map(Store::getId)
                .collect(Collectors.toSet());

        List<UUID> failIds = request.getStoreIds().stream()
                .filter(id -> !foundIdsSet.contains(id))
                .toList();

        List<UUID> successIds = new ArrayList<>();

        for (Store store : foundStores) {
            store.updateVisibility(request.getIsHidden());
            successIds.add(store.getId());
        }

        return StoreVisibilityUpdateResponseDto.of(successIds, failIds, LocalDateTime.now());
    }

    // CUSTOMER X
    @Transactional
    public void deleteStore(UUID storeId, Long userId, UserRoleEnum userRole) {
        Store store = storeRepository.findByIdWithProducts(storeId).orElseThrow(
                () -> new BusinessException(ErrorCode.STORE_NOT_FOUND)
        );
        if (userRole.equals(UserRoleEnum.OWNER)) {
            store.validateOwner(userId);
        }
        store.delete(userId);
    }

    public void validateServiceArea(String region1, String region2) {
        boolean isAvailable = availableAddressRepository.existsByRegion1depthNameAndRegion2depthName(region1, region2);
        if (!isAvailable) {
            throw new BusinessException(ErrorCode.STORE_NOT_SERVICE_AREA);
        }
    }

    // MANAGER, MASTER, OWNER(본인) 검증
    private boolean isAuthorized(Store store, Long userId, UserRoleEnum userRole) {
        return userRole.equals(UserRoleEnum.MASTER) ||
                userRole.equals(UserRoleEnum.MANAGER) ||
                store.getUser().getId().equals(userId);
    }
}
