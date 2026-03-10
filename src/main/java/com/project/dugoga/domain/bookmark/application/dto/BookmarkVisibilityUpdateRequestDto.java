package com.project.dugoga.domain.bookmark.application.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookmarkVisibilityUpdateRequestDto {

    @NotNull(message = "가게 id는 필수입니다.")
    UUID storeId;

    @NotNull(message = "숨김 여부는 필수입니다.")
    Boolean isHidden;
}
