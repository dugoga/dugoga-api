package com.project.dugoga.domain.category.presentation.controller;

import com.project.dugoga.domain.category.application.dto.CategoryCreateRequestDto;
import com.project.dugoga.domain.category.application.dto.CategoryCreateResponseDto;
import com.project.dugoga.domain.category.application.dto.CategoryPageResponseDto;
import com.project.dugoga.domain.category.application.dto.CategoryUpdateRequestDto;
import com.project.dugoga.domain.category.application.dto.CategoryUpdateResponseDto;
import com.project.dugoga.domain.category.application.service.CategoryService;
import com.project.dugoga.global.security.jwt.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
@Tag(name = "카테고리", description = "카테고리 관련 API")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(
            summary = "카테고리 등록",
            description = "카테고리를 등록합니다. 'MASTER' 또는 'MANAGER' 권한을 가진 사용자만 접근 가능합니다."
    )
    @PreAuthorize("hasAnyRole('MASTER', 'MANAGER')")
    @PostMapping
    public ResponseEntity<CategoryCreateResponseDto> createCategory(@Valid @RequestBody CategoryCreateRequestDto dto) {

        CategoryCreateResponseDto category = categoryService.createCategory(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }

    @Operation(
            summary = "카테고리 수정",
            description = "카테고리 정보(코드, 이름)를 수정합니다. 'MASTER' 또는 'MANAGER' 권한을 가진 사용자만 접근 가능합니다."
    )
    @PreAuthorize("hasAnyRole('MASTER', 'MANAGER')")
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryUpdateResponseDto> updateCategory(@PathVariable UUID categoryId,
                                                                    @Valid @RequestBody CategoryUpdateRequestDto dto) {

        CategoryUpdateResponseDto category = categoryService.updateCategory(dto, categoryId);

        return ResponseEntity.ok(category);
    }

    @Operation(
            summary = "카테고리 삭제",
            description = "카테고리를 삭제합니다. 'MASTER' 또는 'MANAGER' 권한을 가진 사용자만 접근 가능합니다."
    )
    @PreAuthorize("hasAnyRole('MASTER', 'MANAGER')")
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID categoryId,
                                               @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getId();
        categoryService.deleteCategory(categoryId, userId);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "카테고리 조회",
            description = "카테고리를 조회합니다. "
                    + "query 파라미터로 카테고리명을 검색할 수 있으며, "
                    + "로그인한 사용자만 접근 가능합니다."
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<CategoryPageResponseDto> getCategories(Pageable pageable,
                                                                 @RequestParam(required = false) String keyword) {

        return ResponseEntity.ok(categoryService.getCategories(keyword, pageable));
    }
}
