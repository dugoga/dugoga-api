package com.project.dugoga.domain.availableaddress.application.service;


import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.then;

import com.project.dugoga.domain.availableaddress.application.dto.AvailableAddressCreateRequestDto;
import com.project.dugoga.domain.availableaddress.application.dto.AvailableAddressCreateResponseDto;
import com.project.dugoga.domain.availableaddress.application.dto.AvailableAddressUpdateRequestDto;
import com.project.dugoga.domain.availableaddress.application.dto.AvailableAddressUpdateResponseDto;
import com.project.dugoga.domain.availableaddress.domain.model.entity.AvailableAddress;
import com.project.dugoga.domain.availableaddress.domain.repository.AvailableAddressRepository;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AvailableAddressServiceTest {

    @Mock
    private AvailableAddressRepository availableAddressRepository;

    @InjectMocks
    private AvailableAddressService availableAddressService;

    @Nested
    @DisplayName("서비스 지역 생성")
    class CreateCategoryTest {

        @Test
        @DisplayName("성공 - 서비스 지역 생성")
        void createAvailableAddress_success() {

            // given
            AvailableAddressCreateRequestDto request = new AvailableAddressCreateRequestDto
                    ("서울시", "강남구");
            AvailableAddress availableAddress = AvailableAddress.of("서울시", "강남구");

            given(availableAddressRepository.existsByRegion1depthNameAndRegion2depthName("서울시", "강남구")).willReturn(false);
            given(availableAddressRepository.save(any(AvailableAddress.class))).willReturn(availableAddress);

            // when
            AvailableAddressCreateResponseDto response = availableAddressService.createAvailableAddress(request);

            // then
            assertThat(response).isNotNull();

            then(availableAddressRepository).should().existsByRegion1depthNameAndRegion2depthName(request.getRegion1depthName(), request.getRegion2depthName());
            then(availableAddressRepository).should().save(any(AvailableAddress.class));

        }

        @Test
        @DisplayName("실패 - 이미 존재하는 서비스 지역이면 예외")
        void createAvailableAddress_exists() {

            // given
            AvailableAddressCreateRequestDto request = new AvailableAddressCreateRequestDto
                    ("서울시", "강남구");

            given(availableAddressRepository.existsByRegion1depthNameAndRegion2depthName("서울시", "강남구")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> availableAddressService.createAvailableAddress(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.AVAILABLE_ADDRESS_ALREADY_EXISTS.getDefaultMessage());
        }

        @Nested
        @DisplayName("서비스 지역 수정")
        class updateCategoryTest {
            @Test
            @DisplayName("성공 - 서비스 지역 수정")
            void updateAvailableAddress_success() {
                // given
                UUID areaId = UUID.randomUUID();
                AvailableAddressUpdateRequestDto request =
                        new AvailableAddressUpdateRequestDto("서울시", "종로구");
                AvailableAddress availableAddress = AvailableAddress.of("서울시", "강남구");

                given(availableAddressRepository.findByIdAndDeletedAtIsNull(areaId)).willReturn(Optional.of(availableAddress));
                given(availableAddressRepository.existsByRegion1depthNameAndRegion2depthName("서울시", "종로구")).willReturn(false);

                // when
                AvailableAddressUpdateResponseDto response = availableAddressService.updateAvailableAddress(areaId, request);

                // then
                assertThat(response).isNotNull();

                then(availableAddressRepository).should().findByIdAndDeletedAtIsNull(areaId);
                then(availableAddressRepository).should().existsByRegion1depthNameAndRegion2depthName(request.getRegion1depthName(), request.getRegion2depthName());

            }

            @Test
            @DisplayName("실패 - 수정할 서비스 지역이 존재하지 않으면 예외")
            void updateAvailableAddress_notFound() {
                // given
                UUID areaId = UUID.randomUUID();
                AvailableAddressUpdateRequestDto request =
                        new AvailableAddressUpdateRequestDto("서울시", "강남구");

                given(availableAddressRepository.findByIdAndDeletedAtIsNull(areaId))
                        .willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> availableAddressService.updateAvailableAddress(areaId, request))
                        .isInstanceOf(BusinessException.class)
                        .hasMessage(ErrorCode.AVAILABLE_ADDRESS_NOT_FOUND.getDefaultMessage());
            }

            @Test
            @DisplayName("실패 - 이미 존재하는 서비스 지역이면 예외")
            void updateAvailableAddress_exists() {
                // given
                UUID areaId = UUID.randomUUID();
                AvailableAddressUpdateRequestDto request = new AvailableAddressUpdateRequestDto
                        ("서울시", "강남구");
                AvailableAddress availableAddress = AvailableAddress.of("서울시", "종로구");

                given(availableAddressRepository.findByIdAndDeletedAtIsNull(areaId)).willReturn(Optional.of(availableAddress));
                given(availableAddressRepository.existsByRegion1depthNameAndRegion2depthName(
                        request.getRegion1depthName(),
                        request.getRegion2depthName()
                )).willReturn(true);
                // when & then
                assertThatThrownBy(() -> availableAddressService.updateAvailableAddress(areaId, request))
                        .isInstanceOf(BusinessException.class)
                        .hasMessage(ErrorCode.AVAILABLE_ADDRESS_ALREADY_EXISTS.getDefaultMessage());

            }
        }

        @Nested
        @DisplayName("서비스 지역 삭제")
        class deleteCategoryTest {

            @Test
            @DisplayName("성공 - 서비스 지역 삭제")
            void deleteAvailableAddress_success() {

                // given
                UUID areaId = UUID.randomUUID();
                Long userId = 1L;
                AvailableAddress availableAddress = AvailableAddress.of("서울시", "강남구");

                given(availableAddressRepository.findByIdAndDeletedAtIsNull(areaId)).willReturn(Optional.of(availableAddress));

                // when
                availableAddressService.deleteAvailableAddress(areaId, userId);

                // then
                assertThat(availableAddress.getDeletedAt()).isNotNull();
                assertThat(availableAddress.getDeletedBy()).isEqualTo(userId);

                then(availableAddressRepository).should().findByIdAndDeletedAtIsNull(areaId);

            }

            @Test
            @DisplayName("실패 - 삭제할 서비스 지역이 존재하지 않으면 예외")
            void deleteAvailableAddress_notFound() {

            // given
            UUID areaId = UUID.randomUUID();
            Long userId = 1L;

            given(availableAddressRepository.findByIdAndDeletedAtIsNull(areaId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> availableAddressService.deleteAvailableAddress(areaId, userId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.AVAILABLE_ADDRESS_NOT_FOUND.getDefaultMessage());
            }


        }
    }

}