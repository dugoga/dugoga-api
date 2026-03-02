package com.project.dugoga.domain.category.presentation.controller;

import com.project.dugoga.domain.category.application.dto.CategoryCreateRequestDto;
import com.project.dugoga.domain.category.application.dto.CategoryCreateResponseDto;
import com.project.dugoga.domain.category.application.dto.CategoryPageResponseDto;
import com.project.dugoga.domain.category.application.dto.CategoryRestoreResponseDto;
import com.project.dugoga.domain.category.application.dto.CategoryUpdateRequestDto;
import com.project.dugoga.domain.category.application.dto.CategoryUpdateResponseDto;
import com.project.dugoga.domain.category.application.dto.CategorySearchDto;
import com.project.dugoga.domain.category.application.service.CategoryService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /*
     *   카테고리 등록
     *   todo: 권한 판단 : MASTER, MANGER
     * */
    @PostMapping
    public ResponseEntity<CategoryCreateResponseDto> createCategory(@Valid @RequestBody CategoryCreateRequestDto dto) {

        CategoryCreateResponseDto category = categoryService.createCategory(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }
    /*
    *   카테고리 수정
    *   : 삭제한 카테고리는 수정 불가능
    *   todo: 권한 판단 : MASTER, MANGER
    * */
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryUpdateResponseDto> updateCategory(@PathVariable UUID categoryId,
                                                                    @Valid @RequestBody CategoryUpdateRequestDto dto) {

        CategoryUpdateResponseDto category = categoryService.updateCategory(dto, categoryId);

        return ResponseEntity.ok(category);
    }

    /*
     *   카테고리 삭제
     *   todo: 권한 판단 : MASTER, MANGER
     * */
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID categoryId) {

        // todo : user 정보(userId) 가져오기
        Long userId = 1L;
        categoryService.deleteCategory(categoryId, userId);

        return ResponseEntity.noContent().build();
    }

    /*
     *   카테고리 삭제 복구
     *   todo: 권한 판단 : MASTER, MANGER
     * */
    @PatchMapping("/{categoryId}")
    public ResponseEntity<CategoryRestoreResponseDto> restoreCategory(@PathVariable UUID categoryId) {

        CategoryRestoreResponseDto category = categoryService.restoreCategory(categoryId);

        return ResponseEntity.ok(category);
    }


    /*
    *  CUSTOMER, OWNER 전용 조회 (삭제된 카테고리 조회 x)
    * */
    @GetMapping
    public ResponseEntity<CategoryPageResponseDto> getCategories(Pageable pageable,
                                                                 @RequestParam(required = false) String keyword) {

        return ResponseEntity.ok(categoryService.getCategories(keyword, pageable));
    }


}
