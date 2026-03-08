package com.project.dugoga.domain.availableaddress.application.dto;

import com.project.dugoga.domain.availableaddress.domain.model.entity.AvailableAddress;
import com.project.dugoga.global.dto.PageInfoDto;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class AvailableAddressListDto {

    private List<AvailableAddressResponse> availableAddress;
    private PageInfoDto pageInfo;

    public static AvailableAddressListDto of(Page<AvailableAddress> availableAddressPage) {
        List<AvailableAddressResponse> list = availableAddressPage.stream()
                .map(AvailableAddressResponse::from)
                .toList();

        return AvailableAddressListDto.builder()
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

        public static AvailableAddressResponse from(AvailableAddress availableAddress) {
            return AvailableAddressResponse.builder()
                    .id(availableAddress.getId())
                    .region1depthName(availableAddress.getRegion1depthName())
                    .region2depthName(availableAddress.getRegion2depthName())
                    .build();
        }
    }
}
