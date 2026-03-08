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
@RequestMapping("/api")
@Tag(name = "카테고리", description = "categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @PreAuthorize("hasAnyRole('MASTER', 'MANAGER')")
    /*
     *   카테고리 등록
     *   todo: 권한 판단 : MASTER, MANGER
     * */
    @Operation(
            summary = "카테고리 등록",
            description = "MASTER 또는 MANAGER 권한을 가진 사용자만 등록할 수 있습니다."
    )
    @PostMapping("/categories")
    public ResponseEntity<CategoryCreateResponseDto> createCategory(@Valid @RequestBody CategoryCreateRequestDto dto) {

        CategoryCreateResponseDto category = categoryService.createCategory(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }

    @PreAuthorize("hasAnyRole('MASTER', 'MANAGER')")
    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<CategoryUpdateResponseDto> updateCategory(@PathVariable UUID categoryId,
                                                                    @Valid @RequestBody CategoryUpdateRequestDto dto) {

        CategoryUpdateResponseDto category = categoryService.updateCategory(dto, categoryId);

        return ResponseEntity.ok(category);
    }


    @PreAuthorize("hasAnyRole('MASTER', 'MANAGER')")
    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID categoryId,
                                               @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getId();
        categoryService.deleteCategory(categoryId, userId);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/categories")
    public ResponseEntity<CategoryPageResponseDto> getCategories(Pageable pageable,
                                                                 @RequestParam(required = false) String keyword) {

        return ResponseEntity.ok(categoryService.getCategories(keyword, pageable));
    }
}
