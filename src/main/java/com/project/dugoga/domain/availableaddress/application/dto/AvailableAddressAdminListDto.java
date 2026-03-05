package com.project.dugoga.domain.availableaddress.application.dto;

import com.project.dugoga.domain.availableaddress.domain.model.entity.AvailableAddress;
import com.project.dugoga.global.dto.PageInfoDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class AvailableAddressAdminListDto {

    private List<AvailableAddressResponse> availableAddress;
    private PageInfoDto pageInfo;

    public static AvailableAddressAdminListDto of(Page<AvailableAddress> availableAddressPage) {
        List<AvailableAddressResponse> list = availableAddressPage.stream()
                .map(AvailableAddressResponse::from)
                .toList();

        return AvailableAddressAdminListDto.builder()
                .availableAddress(list)
                .pageInfo(PageInfoDto.from(availableAddressPage))
                .build();
    }


    @Getter
    @Builder
    private static class AvailableAddressResponse {
        private UUID id;
        private String region1depthName;
        private String region2depthName;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime deletedAt;

        public static AvailableAddressResponse from(AvailableAddress availableAddress) {
            return AvailableAddressResponse.builder()
                    .id(availableAddress.getId())
                    .region1depthName(availableAddress.getRegion1depthName())
                    .region2depthName(availableAddress.getRegion2depthName())
                    .createdAt(availableAddress.getCreatedAt())
                    .updatedAt(availableAddress.getUpdatedAt())
                    .deletedAt(availableAddress.getDeletedAt())
                    .build();
        }
    }
}
