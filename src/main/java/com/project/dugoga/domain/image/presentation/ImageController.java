package com.project.dugoga.domain.image.presentation;

import com.project.dugoga.domain.image.application.dto.ImageDeleteRequestDto;
import com.project.dugoga.domain.image.application.dto.ImageUpdateRequestDto;
import com.project.dugoga.domain.image.application.dto.PresignedUrlRequestDto;
import com.project.dugoga.domain.image.application.dto.PresignedUrlResponseDto;
import com.project.dugoga.domain.image.application.service.ImageService;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/image")

// TODO : 추후 사용자 정보 반영 필요
public class ImageController {

    private final ImageService imageService;

    /*
    *  이미지 업로드, 삭제, 수정만 제공
    *  이미지 가져오기는 각 도메인의 image-url(s3 url)을 해당 도메인의 controller에서 직접 제공
    * */

    @PostMapping("/presigned-url/{domain}")
    public ResponseEntity<PresignedUrlResponseDto>  getPresignedUrl(
            @PathVariable String domain, @Valid @RequestBody PresignedUrlRequestDto requestDto)
    {
        if (domain.equals("reviews")) {
            PresignedUrlResponseDto responseDto = imageService.getPresignedUrl(requestDto, "reviews");
            return ResponseEntity.ok(responseDto);
        } else if (domain.equals("products")) {
            PresignedUrlResponseDto responseDto = imageService.getPresignedUrl(requestDto, "products");
            return ResponseEntity.ok(responseDto);
        } else {
            throw new BusinessException(ErrorCode.DOMAIN_NOT_FOUND);
        }
    }

    @DeleteMapping("/{domain}")
    public ResponseEntity<Void> deleteReviewsPresignedUrl(
        @PathVariable String domain, @Valid @RequestBody ImageDeleteRequestDto requestDto)
    {
        Long userId = 1L;

        if (domain.equals("reviews") || domain.equals("products")) {
            imageService.deleteImage(requestDto);
            return ResponseEntity.noContent().build();
        } else {
            throw new BusinessException(ErrorCode.DOMAIN_NOT_FOUND);
        }
    }

    @PutMapping("/presigned-url/{domain}")
    public ResponseEntity<PresignedUrlResponseDto>  updateReviewsPresignedUrl(
            @PathVariable String domain, @Valid @RequestBody ImageUpdateRequestDto requestDto)
    {
        if (domain.equals("reviews") || domain.equals("products")) {
            // 사진 업데이트의 경우 새로운 사진을 생성하고 기존 사진을 삭제하는 방식으로 구현
            PresignedUrlResponseDto responseDto = imageService.getPresignedUrl(new PresignedUrlRequestDto(requestDto.getFileName(), requestDto.getFileType()), "reviews");
            imageService.deleteImage(new ImageDeleteRequestDto(requestDto.getDeleteUrl()));
            return ResponseEntity.ok(responseDto);
        } else {
            throw new BusinessException(ErrorCode.DOMAIN_NOT_FOUND);
        }
    }
}
