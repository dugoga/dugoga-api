package com.project.dugoga.domain.store.application.dto;

import com.project.dugoga.domain.store.domain.model.enums.StoreStatus;
import com.project.dugoga.domain.user.domain.model.enums.UserRoleEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreStatusUpdateRequestDto {
    // TODO: Principal 도입시 삭제해야 합니다.
    @NotNull(message = "회원 ID는 필수입니다.")
    Long userId;
    @NotNull(message = "회원 권한은 필수입니다.")
    private UserRoleEnum userRole;
    // << 여기까지

    @NotEmpty(message = "최소 한 개 이상의 가게 ID를 입력해야 합니다.")
    @Size(max = 100, message = "한 번에 최대 100개까지만 처리할 수 있습니다.")
    List<UUID> storeIds;

    @NotNull(message = "가게 상태는 필수입니다.")
    StoreStatus status;

}
