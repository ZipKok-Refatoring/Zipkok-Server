package com.project.zipkok.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.project.zipkok.common.exception.s3.FileUploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.CANNOT_SAVE_FILE;

@Slf4j
@RequiredArgsConstructor
@Component
public class FileUploadUtils {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadFile(MultipartFile file) {

        try {
            String fileName = file.getOriginalFilename();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);
            return amazonS3Client.getResourceUrl(bucket, fileName);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new FileUploadException(CANNOT_SAVE_FILE);
        }
    }

    public void deleteFile(String key){
        try {
            this.amazonS3Client.deleteObject(this.bucket, "스크린샷 2024-01-20 115756.png");

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new FileUploadException(CANNOT_SAVE_FILE);
        }
    }
}
