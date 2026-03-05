package com.project.dugoga.domain.image.presentation;

import com.project.dugoga.domain.image.application.dto.ImageDeleteRequestDto;
import com.project.dugoga.domain.image.application.dto.ImageUpdateRequestDto;
import com.project.dugoga.domain.image.application.dto.PresignedUrlRequestDto;
import com.project.dugoga.domain.image.application.dto.PresignedUrlResponseDto;
import com.project.dugoga.domain.image.application.service.ImageService;
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

    @PostMapping("/presigned-url/reviews")
    public ResponseEntity<PresignedUrlResponseDto>  getReviewsPresignedUrl(
            @Valid @RequestBody PresignedUrlRequestDto requestDto)
    {
        PresignedUrlResponseDto responseDto = imageService.getPresignedUrl(requestDto, "reviews");
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/reviews")
    public ResponseEntity<ImageDeleteRequestDto> deleteReviewsPresignedUrl(
        @Valid @RequestBody ImageDeleteRequestDto requestDto)
    {
        Long userId = 1L;
        imageService.deleteImage(requestDto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/presigned-url/reviews")
    public ResponseEntity<PresignedUrlResponseDto>  updateReviewsPresignedUrl(
            @Valid @RequestBody ImageUpdateRequestDto requestDto)
    {
        // 사진 업데이트의 경우 새로운 사진을 생성하고 기존 사진을 삭제하는 방식으로 구현
        PresignedUrlResponseDto responseDto = imageService.getPresignedUrl(new PresignedUrlRequestDto(requestDto.getFileName(), requestDto.getFileType()), "reviews");
        imageService.deleteImage(new ImageDeleteRequestDto(requestDto.getDeleteUrl()));
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/presigned-url/products")
    public ResponseEntity<PresignedUrlResponseDto> getProductsPresignedUrl(
            @Valid @RequestBody PresignedUrlRequestDto requestDto)
    {
        PresignedUrlResponseDto responseDto = imageService.getPresignedUrl(requestDto, "products");
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/products")
    public ResponseEntity<ImageDeleteRequestDto> deleteProductsPresignedUrl(
            @Valid @RequestBody ImageDeleteRequestDto requestDto)
    {
        Long userId = 1L;
        imageService.deleteImage(requestDto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/presigned-url/products")
    public ResponseEntity<PresignedUrlResponseDto>  updateProductsPresignedUrl(
            @Valid @RequestBody ImageUpdateRequestDto requestDto)
    {
        // 사진 업데이트의 경우 새로운 사진을 생성하고 기존 사진을 삭제하는 방식으로 구현
        PresignedUrlResponseDto responseDto = imageService.getPresignedUrl(new PresignedUrlRequestDto(requestDto.getFileName(), requestDto.getFileType()), "products");
        imageService.deleteImage(new ImageDeleteRequestDto(requestDto.getDeleteUrl()));
        return ResponseEntity.ok(responseDto);
    }


}
