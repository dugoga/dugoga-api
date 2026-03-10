package com.project.dugoga.domain.image.presentation;

import com.project.dugoga.domain.image.application.dto.ImageDeleteRequestDto;
import com.project.dugoga.domain.image.application.dto.ImageUpdateRequestDto;
import com.project.dugoga.domain.image.application.dto.PresignedUrlRequestDto;
import com.project.dugoga.domain.image.application.dto.PresignedUrlResponseDto;
import com.project.dugoga.domain.image.application.service.ImageService;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
import com.project.dugoga.global.security.jwt.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/image")
@Tag(name = "이미지", description = "이미지 S3 업로드 및 관리 API")
public class ImageController {

    private final ImageService imageService;

    @Operation(
            summary = "Presigned URL 발급",
            description = "S3에 이미지를 업로드하기 위한 Presigned URL 발급 <br>" +
                    "Path Variable(domain)에 도메인(reviews/products) 입력"
    )
    @PostMapping("/presigned-url/{domain}")
    public ResponseEntity<PresignedUrlResponseDto>  getPresignedUrl(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String domain, @Valid @RequestBody PresignedUrlRequestDto requestDto)
    {
        if (domain.equals("reviews")) {
            PresignedUrlResponseDto responseDto = imageService.getPresignedUrl(requestDto, userDetails.getId(), "reviews");
            return ResponseEntity.ok(responseDto);
        } else if (domain.equals("products")) {
            PresignedUrlResponseDto responseDto = imageService.getPresignedUrl(requestDto, userDetails.getId(), "products");
            return ResponseEntity.ok(responseDto);
        } else {
            throw new BusinessException(ErrorCode.DOMAIN_NOT_FOUND);
        }
    }

    @Operation(
            summary = "이미지 삭제",
            description = "S3에 업로드된 기존 이미지 삭제 <br>" +
                    "Path Variable(domain)에 도메인(reviews/products) 입력"
    )
    @DeleteMapping("/{domain}")
    public ResponseEntity<Void> deleteReviewsPresignedUrl(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String domain, @Valid @RequestBody ImageDeleteRequestDto requestDto)
    {
        if (domain.equals("reviews") || domain.equals("products")) {
            imageService.deleteImage(requestDto);
            return ResponseEntity.noContent().build();
        } else {
            throw new BusinessException(ErrorCode.DOMAIN_NOT_FOUND);
        }
    }

    @Operation(
            summary = "이미지 수정",
            description = "기존 이미지를 삭제하고, 새로운 이미지를 업로드하기 위한 Presigned URL을 발급 <br>" +
                    "Path Variable(domain)에 도메인(reviews/products) 입력"
    )
    @PutMapping("/presigned-url/{domain}")
    public ResponseEntity<PresignedUrlResponseDto>  updateReviewsPresignedUrl(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String domain, @Valid @RequestBody ImageUpdateRequestDto requestDto)
    {
        if (domain.equals("reviews") || domain.equals("products")) {
            PresignedUrlResponseDto responseDto = imageService.getPresignedUrl(new PresignedUrlRequestDto(requestDto.getFileName(), requestDto.getFileType()), "reviews");
            imageService.deleteImage(new ImageDeleteRequestDto(requestDto.getDeleteUrl()));
            return ResponseEntity.ok(responseDto);
        } else {
            throw new BusinessException(ErrorCode.DOMAIN_NOT_FOUND);
        }
    }
}
