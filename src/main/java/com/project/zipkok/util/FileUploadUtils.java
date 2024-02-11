package com.project.zipkok.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.project.zipkok.common.exception.s3.FileUploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
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

    public String updateFileDir(String key, String dirName) throws IOException {

        log.info("[FileUploadUtils.updateFileDir]");

        try {

            MultipartFile file = downloadAsMultipartFile(bucket, key);

            String newKey = dirName + "/" + deleteFile(key);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            amazonS3Client.putObject(bucket, newKey, file.getInputStream(), metadata);
            return amazonS3Client.getResourceUrl(bucket, newKey);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new FileUploadException(CANNOT_SAVE_FILE);
        }

    }

    public String deleteFile(String key){

        log.info("[FileUploadUtils.deleteFile]");

        try {
            this.amazonS3Client.deleteObject(this.bucket, key);
            return key;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new FileUploadException(CANNOT_SAVE_FILE);
        }
    }

    public MultipartFile downloadAsMultipartFile(String bucketName, String keyName) throws IOException {

        log.info("[FileUploadUtils.downloadAsMultipartFile]");

        S3Object s3Object = amazonS3Client.getObject(bucketName, keyName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        MultipartFile multipartFile = new MockMultipartFile(
                keyName,
                keyName,
                s3Object.getObjectMetadata().getContentType(),
                inputStream
        );

        return multipartFile;
    }
}
