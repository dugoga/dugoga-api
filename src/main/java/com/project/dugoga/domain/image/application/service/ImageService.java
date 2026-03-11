package com.project.dugoga.domain.image.application.service;

import com.project.dugoga.domain.image.application.dto.ImageDeleteRequestDto;
import com.project.dugoga.domain.image.application.dto.ImageUpdateRequestDto;
import com.project.dugoga.domain.image.application.dto.PresignedUrlRequestDto;
import com.project.dugoga.domain.image.application.dto.PresignedUrlResponseDto;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ImageService {

    private final S3Presigner s3Presigner;
    private final S3Client s3Manager;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.s3.presigned-url-expiration}")
    private int expirationMinutes;

    public PresignedUrlResponseDto getPresignedUrl(PresignedUrlRequestDto requestDto, Long userId, String domain) {
        String fileExtension = extractExtension(requestDto.getFileName());
        String uuid = UUID.randomUUID().toString();
        String key = domain + "/" + userId + "/" + uuid + fileExtension;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(requestDto.getFileType().getMimeType())
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(
            PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(expirationMinutes))
                    .putObjectRequest(putObjectRequest)
                    .build()
        );

        String uploadUrl = presignedRequest.url().toString();
        String fileUrl = "https://" + bucketName + ".s3.amazonaws.com/" + key;

        return PresignedUrlResponseDto.builder()
                .uploadUrl(uploadUrl)
                .fileUrl(fileUrl)
                .build();
    }

    public void deleteImage(ImageDeleteRequestDto requestDto) {
        try {
            String fileUrl = requestDto.getFileUrl();
            String key = extractKey(fileUrl);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Manager.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FILE_URL_NOT_FOUND);
        }
    }

    private String extractExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.'));
    }

    private String extractKey(String fileUrl) {
        String splitStr = ".com/";
        int index = fileUrl.indexOf(splitStr);
        if (index == -1) {
            throw new BusinessException(ErrorCode.FILE_URL_NOT_FOUND);
        }
        return fileUrl.substring(index + splitStr.length());
    }

}
